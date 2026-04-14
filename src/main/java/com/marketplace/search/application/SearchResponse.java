package com.marketplace.search.application;

import com.marketplace.search.domain.SearchResult;

import java.util.List;

/**
 * Paginated response containing search results.
 */
public record SearchResponse(
    List<SearchResult> results,
    long totalElements,
    int totalPages,
    int page
) {
}
