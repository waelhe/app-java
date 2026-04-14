package com.marketplace.review.infrastructure;

import com.marketplace.review.domain.Review;
import com.marketplace.review.domain.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepository {

    private final JpaReviewRepository jpaRepository;

    @Override
    public Review save(Review review) {
        return jpaRepository.save(review);
    }

    @Override
    public Optional<Review> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Review> findByRevieweeId(UUID revieweeId) {
        return jpaRepository.findByRevieweeId(revieweeId);
    }

    @Override
    public List<Review> findByListingId(UUID listingId) {
        return jpaRepository.findByListingId(listingId);
    }
}
