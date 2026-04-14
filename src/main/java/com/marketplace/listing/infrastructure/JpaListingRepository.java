package com.marketplace.listing.infrastructure;

import com.marketplace.listing.domain.Listing;
import com.marketplace.listing.domain.ListingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

interface JpaListingRepository extends JpaRepository<Listing, UUID> {
    Page<Listing> findByProviderId(UUID providerId, Pageable pageable);
    Page<Listing> findByCategoryAndStatus(String category, ListingStatus status, Pageable pageable);
    Page<Listing> findByStatus(ListingStatus status, Pageable pageable);
}
