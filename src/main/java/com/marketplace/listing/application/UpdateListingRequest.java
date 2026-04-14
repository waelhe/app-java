package com.marketplace.listing.application;

import com.marketplace.shared.domain.Money;

public record UpdateListingRequest(
    String title,
    String description,
    String category,
    String subcategory,
    Money price,
    String pricingType,
    String location,
    Double latitude,
    Double longitude,
    String amenities,
    String rules
) {}
