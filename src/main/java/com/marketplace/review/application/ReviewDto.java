package com.marketplace.review.application;

import com.marketplace.review.domain.Review;

import java.time.Instant;
import java.util.UUID;

public record ReviewDto(
    UUID id,
    UUID bookingId,
    UUID reviewerId,
    UUID revieweeId,
    UUID listingId,
    int rating,
    int communicationRating,
    int qualityRating,
    int valueRating,
    String comment,
    String providerResponse,
    Instant respondedAt,
    Instant createdAt
) {
    public static ReviewDto from(Review review) {
        return new ReviewDto(
            review.getId(),
            review.getBookingId(),
            review.getReviewerId(),
            review.getRevieweeId(),
            review.getListingId(),
            review.getRating(),
            review.getCommunicationRating(),
            review.getQualityRating(),
            review.getValueRating(),
            review.getComment(),
            review.getProviderResponse(),
            review.getRespondedAt(),
            review.getCreatedAt()
        );
    }
}
