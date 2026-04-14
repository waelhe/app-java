package com.marketplace.search.application;

import com.marketplace.search.domain.SearchResult;
import com.marketplace.search.domain.Suggestion;
import com.marketplace.search.infrastructure.ListingSearchRepository;
import com.marketplace.shared.domain.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class SearchService {

    private final ListingSearchRepository listingSearchRepository;
    private static final String ACTIVE_STATUS = "ACTIVE";

    public SearchResponse search(String query, String category, BigDecimal minPrice,
                                  BigDecimal maxPrice, String location, Pageable pageable) {
        log.debug("Searching listings: query={}, category={}", query, category);

        try {
            List<ListingDocument> allDocs = listingSearchRepository.findByStatus(ACTIVE_STATUS);

            // Apply filters
            Stream<ListingDocument> stream = allDocs.stream();
            if (query != null && !query.isBlank()) {
                String q = query.toLowerCase();
                stream = stream.filter(d ->
                    (d.getTitle() != null && d.getTitle().toLowerCase().contains(q)) ||
                    (d.getDescription() != null && d.getDescription().toLowerCase().contains(q)));
            }
            if (category != null && !category.isBlank()) {
                stream = stream.filter(d -> category.equals(d.getCategory()));
            }
            if (minPrice != null) {
                stream = stream.filter(d -> d.getPrice() != null && d.getPrice().compareTo(minPrice) >= 0);
            }
            if (maxPrice != null) {
                stream = stream.filter(d -> d.getPrice() != null && d.getPrice().compareTo(maxPrice) <= 0);
            }
            if (location != null && !location.isBlank()) {
                stream = stream.filter(d -> d.getLocation() != null && d.getLocation().toLowerCase().contains(location.toLowerCase()));
            }

            List<ListingDocument> filtered = stream.collect(Collectors.toList());
            long totalElements = filtered.size();

            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), filtered.size());
            List<ListingDocument> pageContent = start < filtered.size()
                    ? filtered.subList(start, end) : List.of();

            List<SearchResult> results = pageContent.stream()
                    .map(this::toSearchResult)
                    .collect(Collectors.toList());

            int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
            return new SearchResponse(results, totalElements, totalPages, pageable.getPageNumber());
        } catch (Exception e) {
            log.error("Search failed: {}", e.getMessage());
            return new SearchResponse(List.of(), 0, 0, 0);
        }
    }

    public List<Suggestion> suggest(String query) {
        if (query == null || query.isBlank()) return List.of();

        String prefix = query.toLowerCase();
        List<Suggestion> suggestions = new ArrayList<>();

        try {
            List<ListingDocument> allActive = listingSearchRepository.findByStatus(ACTIVE_STATUS);

            allActive.stream()
                .filter(d -> d.getCategory() != null && d.getCategory().toLowerCase().startsWith(prefix))
                .collect(Collectors.groupingBy(ListingDocument::getCategory, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new Suggestion(e.getKey(), Suggestion.TYPE_CATEGORY, e.getValue()))
                .forEach(suggestions::add);

            allActive.stream()
                .filter(d -> d.getLocation() != null && d.getLocation().toLowerCase().contains(prefix))
                .collect(Collectors.groupingBy(ListingDocument::getLocation, Collectors.counting()))
                .entrySet().stream()
                .map(e -> new Suggestion(e.getKey(), Suggestion.TYPE_LOCATION, e.getValue()))
                .forEach(suggestions::add);

            suggestions.sort(Comparator.comparingLong(Suggestion::count).reversed());
            if (suggestions.size() > 10) suggestions = suggestions.subList(0, 10);
        } catch (Exception e) {
            log.error("Suggestion query failed: {}", e.getMessage());
        }
        return new ArrayList<>(suggestions);
    }

    public void indexListing(ListingDocument document) {
        log.info("Indexing listing {}: {}", document.getId(), document.getTitle());
        try {
            listingSearchRepository.save(document);
        } catch (Exception e) {
            log.error("Failed to index listing {}: {}", document.getId(), e.getMessage());
        }
    }

    public void removeListing(UUID listingId) {
        log.info("Removing listing {} from search index", listingId);
        try {
            listingSearchRepository.deleteById(listingId);
        } catch (Exception e) {
            log.error("Failed to remove listing {}: {}", listingId, e.getMessage());
        }
    }

    private SearchResult toSearchResult(ListingDocument doc) {
        return new SearchResult(
                doc.getId(), doc.getTitle(), doc.getDescription(), doc.getCategory(),
                Money.of(doc.getPrice(), doc.getCurrency()),
                doc.getLocation(), doc.getProviderId(), 1.0);
    }
}
