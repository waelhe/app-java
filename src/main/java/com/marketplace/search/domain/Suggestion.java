package com.marketplace.search.domain;

/**
 * Represents an autocomplete suggestion returned by the search engine.
 * Suggestions can be of different types: category, subcategory, or location.
 * The count indicates how many listings match this suggestion.
 */
public record Suggestion(
    String text,
    String type,
    long count
) {
    /**
     * Valid suggestion types.
     */
    public static final String TYPE_CATEGORY = "category";
    public static final String TYPE_SUBCATEGORY = "subcategory";
    public static final String TYPE_LOCATION = "location";
}
