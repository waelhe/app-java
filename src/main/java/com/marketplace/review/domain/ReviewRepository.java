package com.marketplace.review.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewRepository {
    Review save(Review review);
    Optional<Review> findById(UUID id);
    List<Review> findByRevieweeId(UUID revieweeId);
    List<Review> findByListingId(UUID listingId);
}
