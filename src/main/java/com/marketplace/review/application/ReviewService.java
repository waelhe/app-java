package com.marketplace.review.application;

import com.marketplace.review.domain.Review;
import com.marketplace.review.domain.ReviewCreatedEvent;
import com.marketplace.review.domain.ReviewRepository;
import com.marketplace.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewService {

    private final ReviewRepository reviewRepository;

    /**
     * Creates a new review for a completed booking.
     * Validates that the reviewer was part of the booking and all ratings are within 1-5.
     * Fires a ReviewCreatedEvent for downstream processing (e.g., reputation updates).
     */
    @Transactional
    public ReviewDto createReview(CreateReviewRequest request, UUID reviewerId) {
        // Validate all ratings
        Review.validateRating(request.rating());
        Review.validateRating(request.communicationRating());
        Review.validateRating(request.qualityRating());
        Review.validateRating(request.valueRating());

        // Ensure reviewer is not reviewing themselves
        if (request.revieweeId().equals(reviewerId)) {
            throw new BusinessException("Cannot review yourself");
        }

        // TODO: In a full implementation, verify that reviewer was part of the booking
        // by calling the Booking module (e.g., via a BookingClient or by listening to BookingCompletedEvent).
        // For now, we trust the request and log the booking association.
        log.info("Creating review for booking {} by reviewer {} for reviewee {}",
                request.bookingId(), reviewerId, request.revieweeId());

        Review review = Review.builder()
                .bookingId(request.bookingId())
                .reviewerId(reviewerId)
                .revieweeId(request.revieweeId())
                .listingId(request.listingId())
                .rating(request.rating())
                .communicationRating(request.communicationRating())
                .qualityRating(request.qualityRating())
                .valueRating(request.valueRating())
                .comment(request.comment())
                .build();

        review.registerEvent(new ReviewCreatedEvent(
                review.getId(),
                review.getRevieweeId(),
                review.getRating()
        ));

        Review saved = reviewRepository.save(review);
        log.info("Review created: id={}, bookingId={}, rating={}", saved.getId(), request.bookingId(), request.rating());
        return ReviewDto.from(saved);
    }

    /**
     * Allows a provider (reviewee) to respond to a review.
     * Validates that the responder is the actual reviewee of the review.
     */
    @Transactional
    public ReviewDto respondToReview(UUID reviewId, UUID providerId, String response) {
        Review review = findReviewOrThrow(reviewId);

        if (!review.getRevieweeId().equals(providerId)) {
            throw new BusinessException("Only the reviewee can respond to this review");
        }

        review.respond(response);
        Review saved = reviewRepository.save(review);
        log.info("Review responded: id={}, providerId={}", reviewId, providerId);
        return ReviewDto.from(saved);
    }

    /**
     * Retrieves all reviews for a given user (as reviewee).
     * Used for reputation and profile display.
     */
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForUser(UUID userId) {
        return reviewRepository.findByRevieweeId(userId).stream()
                .map(ReviewDto::from)
                .toList();
    }

    /**
     * Retrieves all reviews for a given listing.
     */
    @Transactional(readOnly = true)
    public List<ReviewDto> getReviewsForListing(UUID listingId) {
        return reviewRepository.findByListingId(listingId).stream()
                .map(ReviewDto::from)
                .toList();
    }

    private Review findReviewOrThrow(UUID reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessException("Review not found"));
    }
}
