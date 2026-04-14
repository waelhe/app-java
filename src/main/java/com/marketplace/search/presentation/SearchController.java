package com.marketplace.search.presentation;

import com.marketplace.search.application.SearchRequest;
import com.marketplace.search.application.SearchResponse;
import com.marketplace.search.application.SearchService;
import com.marketplace.search.domain.Suggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * REST controller for the Search module.
 * Provides public endpoints for searching listings and getting
 * autocomplete suggestions. No authentication required.
 */
@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
@NamedInterface("api")
public class SearchController {

    private final SearchService searchService;

    /**
     * Search for listings with optional filters.
     * All parameters are optional — an empty request returns all active listings.
     *
     * @param query     full-text search query
     * @param category  filter by category
     * @param minPrice  minimum price filter
     * @param maxPrice  maximum price filter
     * @param location  filter by location
     * @param page      page number (0-based, default 0)
     * @param size      page size (default 20)
     * @return paginated search response
     */
    @GetMapping
    public ResponseEntity<SearchResponse> search(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) String location,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "20") Integer size) {

        SearchRequest searchRequest = new SearchRequest(query, category, minPrice, maxPrice, location, page, size);

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(
                searchRequest.effectivePage(),
                searchRequest.effectiveSize()
        );

        SearchResponse response = searchService.search(
                searchRequest.query(),
                searchRequest.category(),
                searchRequest.minPrice(),
                searchRequest.maxPrice(),
                searchRequest.location(),
                pageable
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Get autocomplete suggestions for a given prefix.
     * Returns matching categories, subcategories, and locations.
     *
     * @param query the prefix text to match
     * @return list of suggestions with counts
     */
    @GetMapping("/suggest")
    public ResponseEntity<List<Suggestion>> suggest(@RequestParam String query) {
        List<Suggestion> suggestions = searchService.suggest(query);
        return ResponseEntity.ok(suggestions);
    }
}
