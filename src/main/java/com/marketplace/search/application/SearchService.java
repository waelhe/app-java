package com.marketplace.search.application;

import com.marketplace.search.domain.SearchResult;
import com.marketplace.search.domain.Suggestion;
import com.marketplace.search.infrastructure.ListingSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for searching listings in the Elasticsearch index.
 * Provides full-text search with filtering by category, price range,
 * and location, as well as autocomplete suggestions.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final ListingSearchRepository listingSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    private static final String ACTIVE_STATUS = "ACTIVE";

    /**
     * Search for listings with optional filters and full-text query.
     *
     * @param query     full-text search query (optional)
     * @param category  filter by category (optional)
     * @param minPrice  minimum price filter (optional)
     * @param maxPrice  maximum price filter (optional)
     * @param location  filter by location substring (optional)
     * @param pageable  pagination information
     * @return paginated search response with results and metadata
     */
    @Transactional(readOnly = true)
    public SearchResponse search(String query, String category, BigDecimal minPrice,
                                  BigDecimal maxPrice, String location,
                                  org.springframework.data.domain.Pageable pageable) {
        log.debug("Searching listings: query={}, category={}, minPrice={}, maxPrice={}, location={}, page={}",
                query, category, minPrice, maxPrice, location, pageable);

        try {
            SearchHits<ListingDocument> searchHits = executeSearch(query, category, minPrice, maxPrice, location, pageable);
            List<SearchResult> results = searchHits.getSearchHits().stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());

            long totalElements = searchHits.getTotalHits();
            int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

            return new SearchResponse(results, totalElements, totalPages, pageable.getPageNumber());
        } catch (Exception e) {
            log.error("Search failed, falling back to repository query: {}", e.getMessage());
            return fallbackSearch(category, location, pageable);
        }
    }

    /**
     * Get autocomplete suggestions based on a prefix query.
     * Returns suggestions for categories, subcategories, and locations
     * that match the given prefix.
     *
     * @param query the prefix text to match
     * @return list of suggestions with counts
     */
    @Transactional(readOnly = true)
    public List<Suggestion> suggest(String query) {
        log.debug("Getting suggestions for query: {}", query);

        if (query == null || query.isBlank()) {
            return List.of();
        }

        List<Suggestion> suggestions = new ArrayList<>();
        String prefix = query.toLowerCase();

        try {
            // Aggregate categories matching the prefix
            List<ListingDocument> allActive = listingSearchRepository.findByStatus(ACTIVE_STATUS);

            // Category suggestions
            Map<String, Long> categoryCounts = allActive.stream()
                    .filter(doc -> doc.getCategory() != null && doc.getCategory().toLowerCase().startsWith(prefix))
                    .collect(Collectors.groupingBy(ListingDocument::getCategory, Collectors.counting()));

            categoryCounts.forEach((text, count) ->
                    suggestions.add(new Suggestion(text, Suggestion.TYPE_CATEGORY, count)));

            // Subcategory suggestions
            Map<String, Long> subcategoryCounts = allActive.stream()
                    .filter(doc -> doc.getSubcategory() != null && doc.getSubcategory().toLowerCase().startsWith(prefix))
                    .collect(Collectors.groupingBy(ListingDocument::getSubcategory, Collectors.counting()));

            subcategoryCounts.forEach((text, count) ->
                    suggestions.add(new Suggestion(text, Suggestion.TYPE_SUBCATEGORY, count)));

            // Location suggestions
            Map<String, Long> locationCounts = allActive.stream()
                    .filter(doc -> doc.getLocation() != null && doc.getLocation().toLowerCase().contains(prefix))
                    .collect(Collectors.groupingBy(ListingDocument::getLocation, Collectors.counting()));

            locationCounts.forEach((text, count) ->
                    suggestions.add(new Suggestion(text, Suggestion.TYPE_LOCATION, count)));

            // Sort by count descending, limit to top 10
            suggestions.sort(Comparator.comparingLong(Suggestion::count).reversed());
            if (suggestions.size() > 10) {
                suggestions = suggestions.subList(0, 10);
            }

        } catch (Exception e) {
            log.error("Suggestion query failed: {}", e.getMessage());
        }

        return suggestions;
    }

    /**
     * Index a listing document into Elasticsearch.
     * Called when a listing is activated or updated.
     *
     * @param document the listing document to index
     */
    public void indexListing(ListingDocument document) {
        log.info("Indexing listing {}: {}", document.getId(), document.getTitle());
        try {
            listingSearchRepository.save(document);
            log.debug("Successfully indexed listing {}", document.getId());
        } catch (Exception e) {
            log.error("Failed to index listing {}: {}", document.getId(), e.getMessage(), e);
        }
    }

    /**
     * Remove a listing from the Elasticsearch index.
     * Called when a listing is deactivated or deleted.
     *
     * @param listingId the ID of the listing to remove
     */
    public void removeListing(UUID listingId) {
        log.info("Removing listing {} from search index", listingId);
        try {
            listingSearchRepository.deleteById(listingId);
            log.debug("Successfully removed listing {} from index", listingId);
        } catch (Exception e) {
            log.error("Failed to remove listing {} from index: {}", listingId, e.getMessage(), e);
        }
    }

    /**
     * Execute the Elasticsearch search using a NativeQuery with bool/must/filter clauses.
     */
    private SearchHits<ListingDocument> executeSearch(String query, String category,
                                                       BigDecimal minPrice, BigDecimal maxPrice,
                                                       String location,
                                                       org.springframework.data.domain.Pageable pageable) {
        var boolQuery = co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery.of(b -> {
            // Always filter for active listings
            b.filter(f -> f.term(t -> t.field("status").value(ACTIVE_STATUS)));

            // Full-text query on title and description
            if (query != null && !query.isBlank()) {
                b.must(m -> m.multiMatch(mm -> mm
                        .fields("title", "description")
                        .query(query)
                        .type(co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType.BestFields)
                        .fuzziness("AUTO")
                ));
            }

            // Category filter
            if (category != null && !category.isBlank()) {
                b.filter(f -> f.term(t -> t.field("category").value(category)));
            }

            // Price range filters
            if (minPrice != null) {
                b.filter(f -> f.range(r -> r.field("price").gte(co.elastic.clients.json.JsonData.of(minPrice))));
            }
            if (maxPrice != null) {
                b.filter(f -> f.range(r -> r.field("price").lte(co.elastic.clients.json.JsonData.of(maxPrice))));
            }

            // Location filter
            if (location != null && !location.isBlank()) {
                b.filter(f -> f.wildcard(w -> w.field("location").value("*" + location + "*")));
            }

            return b;
        });

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(q -> q.bool(boolQuery._toQuery()))
                .withPageable(pageable)
                .build();

        return elasticsearchOperations.search(searchQuery, ListingDocument.class);
    }

    /**
     * Fallback search using repository methods when Elasticsearch query fails.
     */
    private SearchResponse fallbackSearch(String category, String location,
                                           org.springframework.data.domain.Pageable pageable) {
        List<ListingDocument> documents;

        if (category != null && !category.isBlank()) {
            documents = listingSearchRepository.findByCategoryAndStatus(category, ACTIVE_STATUS);
        } else if (location != null && !location.isBlank()) {
            documents = listingSearchRepository.findByLocationContainingAndStatus(location, ACTIVE_STATUS);
        } else {
            documents = listingSearchRepository.findByStatus(ACTIVE_STATUS);
        }

        // Manual pagination
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), documents.size());
        List<ListingDocument> pageContent = start < documents.size()
                ? documents.subList(start, end)
                : List.of();

        List<SearchResult> results = pageContent.stream()
                .map(doc -> new SearchResult(
                        doc.getId(), doc.getTitle(), doc.getDescription(),
                        doc.getCategory(),
                        com.marketplace.shared.domain.Money.of(doc.getPrice(), doc.getCurrency()),
                        doc.getLocation(), doc.getProviderId(), 1.0))
                .collect(Collectors.toList());

        long totalElements = documents.size();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        return new SearchResponse(results, totalElements, totalPages, pageable.getPageNumber());
    }

    /**
     * Convert a SearchHit to a SearchResult domain object.
     */
    private SearchResult toSearchResult(SearchHit<ListingDocument> hit) {
        ListingDocument doc = hit.getContent();
        return new SearchResult(
                doc.getId(),
                doc.getTitle(),
                doc.getDescription(),
                doc.getCategory(),
                com.marketplace.shared.domain.Money.of(doc.getPrice(), doc.getCurrency()),
                doc.getLocation(),
                doc.getProviderId(),
                hit.getScore()
        );
    }
}
