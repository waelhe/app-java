package com.marketplace.search.application;

import java.math.BigDecimal;

/**
 * Request parameters for listing search.
 * All fields are optional — an empty request returns all active listings
 * with default pagination.
 */
public record SearchRequest(
    String query,
    String category,
    BigDecimal minPrice,
    BigDecimal maxPrice,
    String location,
    Integer page,
    Integer size
) {
    /**
     * Default page number if not specified.
     */
    public static final int DEFAULT_PAGE = 0;

    /**
     * Default page size if not specified.
     */
    public static final int DEFAULT_SIZE = 20;

    /**
     * Returns the effective page number, defaulting to 0 if not specified.
     */
    public int effectivePage() {
        return page != null ? page : DEFAULT_PAGE;
    }

    /**
     * Returns the effective page size, defaulting to 20 if not specified.
     */
    public int effectiveSize() {
        return size != null ? size : DEFAULT_SIZE;
    }
}
