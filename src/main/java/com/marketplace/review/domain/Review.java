package com.marketplace.review.domain;

import com.marketplace.shared.domain.AggregateRoot;
import com.marketplace.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "reviews")
public class Review extends AggregateRoot {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID reviewerId;

    @Column(nullable = false)
    private UUID revieweeId;

    @Column(nullable = false)
    private UUID listingId;

    @Column(nullable = false)
    private int rating;

    @Column(nullable = false)
    private int communicationRating;

    @Column(nullable = false)
    private int qualityRating;

    @Column(nullable = false)
    private int valueRating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(columnDefinition = "TEXT")
    private String providerResponse;

    private Instant respondedAt;

    /**
     * Allows the reviewee (provider) to respond to a review.
     * Can only respond once.
     */
    public void respond(String response) {
        if (this.providerResponse != null) {
            throw new BusinessException("Review already has a provider response");
        }
        if (response == null || response.isBlank()) {
            throw new BusinessException("Response cannot be empty");
        }
        this.providerResponse = response;
        this.respondedAt = Instant.now();
    }

    /**
     * Validates that all rating values are within the 1-5 range.
     */
    public static void validateRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }
    }
}
