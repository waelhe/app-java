package com.marketplace.listing.application;

import com.marketplace.shared.domain.Money;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateListingRequest(
    @NotBlank String title,
    String description,
    @NotBlank String category,
    String subcategory,
    @NotNull Money price,
    String pricingType,
    String location,
    Double latitude,
    Double longitude,
    String amenities,
    String rules
) {}
