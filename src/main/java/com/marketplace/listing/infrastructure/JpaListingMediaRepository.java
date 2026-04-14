package com.marketplace.listing.infrastructure;

import com.marketplace.listing.domain.ListingMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaListingMediaRepository extends JpaRepository<ListingMedia, UUID> {
    List<ListingMedia> findByListingIdOrderByDisplayOrderAsc(UUID listingId);
    void deleteByListingId(UUID listingId);
}
