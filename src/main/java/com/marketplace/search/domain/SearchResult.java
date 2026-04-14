package com.marketplace.search.domain;

import com.marketplace.shared.domain.Money;

import java.util.UUID;

/**
 * Represents a single search result from the search index.
 * Maps Elasticsearch document data into a domain-level read model
 * with relevance scoring information.
 */
public record SearchResult(
    UUID id,
    String title,
    String description,
    String category,
    Money price,
    String location,
    UUID providerId,
    double score
) {
}
