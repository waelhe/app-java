package com.marketplace.listing.infrastructure;

import com.marketplace.listing.domain.Listing;
import com.marketplace.listing.domain.ListingRepository;
import com.marketplace.listing.domain.ListingStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class ListingRepositoryImpl implements ListingRepository {

    private final JpaListingRepository jpaRepository;

    @Override
    public Listing save(Listing listing) {
        return jpaRepository.save(listing);
    }

    @Override
    public Optional<Listing> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Page<Listing> findByProviderId(UUID providerId, Pageable pageable) {
        return jpaRepository.findByProviderId(providerId, pageable);
    }

    @Override
    public Page<Listing> findByCategoryAndStatus(String category, ListingStatus status, Pageable pageable) {
        return jpaRepository.findByCategoryAndStatus(category, status, pageable);
    }

    @Override
    public Page<Listing> findByStatus(ListingStatus status, Pageable pageable) {
        return jpaRepository.findByStatus(status, pageable);
    }
}
