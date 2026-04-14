package com.marketplace.review.application;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record CreateReviewRequest(
    @NotNull UUID bookingId,
    @NotNull UUID revieweeId,
    @NotNull UUID listingId,
    @NotNull @Min(1) @Max(5) int rating,
    @NotNull @Min(1) @Max(5) int communicationRating,
    @NotNull @Min(1) @Max(5) int qualityRating,
    @NotNull @Min(1) @Max(5) int valueRating,
    String comment
) {}
