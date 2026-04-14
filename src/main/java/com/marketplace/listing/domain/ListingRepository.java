package com.marketplace.listing.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface ListingRepository {
    Listing save(Listing listing);
    Optional<Listing> findById(UUID id);
    Page<Listing> findByProviderId(UUID providerId, Pageable pageable);
    Page<Listing> findByCategoryAndStatus(String category, ListingStatus status, Pageable pageable);
    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);
}
