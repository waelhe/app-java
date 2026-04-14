package com.marketplace.listing.application;

import com.marketplace.listing.domain.Listing;
import com.marketplace.listing.domain.ListingStatus;
import com.marketplace.shared.domain.Money;

import java.time.Instant;
import java.util.UUID;

public record ListingDto(
    UUID id,
    UUID providerId,
    String title,
    String description,
    String category,
    String subcategory,
    Money price,
    String pricingType,
    ListingStatus status,
    String location,
    Double latitude,
    Double longitude,
    String amenities,
    String rules,
    Instant createdAt
) {
    public static ListingDto from(Listing listing) {
        return new ListingDto(
            listing.getId(), listing.getProviderId(), listing.getTitle(),
            listing.getDescription(), listing.getCategory(), listing.getSubcategory(),
            listing.getPrice(), listing.getPricingType(), listing.getStatus(),
            listing.getLocation(), listing.getLatitude(), listing.getLongitude(),
            listing.getAmenities(), listing.getRules(), listing.getCreatedAt()
        );
    }
}
