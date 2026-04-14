package com.marketplace.review.infrastructure;

import com.marketplace.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaReviewRepository extends JpaRepository<Review, UUID> {
    List<Review> findByRevieweeId(UUID revieweeId);
    List<Review> findByListingId(UUID listingId);
}
