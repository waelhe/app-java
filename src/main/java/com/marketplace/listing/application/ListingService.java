package com.marketplace.listing.application;

import com.marketplace.shared.exception.BusinessException;
import com.marketplace.listing.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ListingService {

    private final ListingRepository listingRepository;

    @Transactional
    public ListingDto createListing(CreateListingRequest request, UUID providerId) {
        Listing listing = Listing.builder()
            .providerId(providerId)
            .title(request.title())
            .description(request.description())
            .category(request.category())
            .subcategory(request.subcategory())
            .price(request.price())
            .pricingType(request.pricingType())
            .status(ListingStatus.DRAFT)
            .location(request.location())
            .latitude(request.latitude())
            .longitude(request.longitude())
            .amenities(request.amenities())
            .rules(request.rules())
            .build();

        listing = listingRepository.save(listing);
        log.info("Listing created: id={}, providerId={}", listing.getId(), providerId);
        return ListingDto.from(listing);
    }

    @Transactional(readOnly = true)
    public ListingDto getListing(UUID listingId) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException("Listing not found"));
        return ListingDto.from(listing);
    }

    @Transactional
    public ListingDto updateListing(UUID listingId, UUID providerId, UpdateListingRequest request) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException("Listing not found"));

        if (!listing.getProviderId().equals(providerId)) {
            throw new BusinessException("Not authorized to update this listing");
        }

        listing.updateDetails(request.title(), request.description(), request.category(),
            request.subcategory(), request.price(), request.pricingType(),
            request.location(), request.latitude(), request.longitude(),
            request.amenities(), request.rules());

        listing = listingRepository.save(listing);
        return ListingDto.from(listing);
    }

    @Transactional
    public void activateListing(UUID listingId, UUID providerId) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException("Listing not found"));
        if (!listing.getProviderId().equals(providerId)) {
            throw new BusinessException("Not authorized");
        }
        listing.activate();
        listingRepository.save(listing);
        log.info("Listing activated: id={}", listingId);
    }

    @Transactional
    public void deactivateListing(UUID listingId, UUID providerId) {
        Listing listing = listingRepository.findById(listingId)
            .orElseThrow(() -> new BusinessException("Listing not found"));
        if (!listing.getProviderId().equals(providerId)) {
            throw new BusinessException("Not authorized");
        }
        listing.deactivate();
        listingRepository.save(listing);
    }

    @Transactional(readOnly = true)
    public Page<ListingDto> getProviderListings(UUID providerId, Pageable pageable) {
        return listingRepository.findByProviderId(providerId, pageable).map(ListingDto::from);
    }

    @Transactional(readOnly = true)
    public Page<ListingDto> searchListings(String category, Pageable pageable) {
        if (category != null) {
            return listingRepository.findByCategoryAndStatus(category, ListingStatus.ACTIVE, pageable)
                .map(ListingDto::from);
        }
        return listingRepository.findByStatus(ListingStatus.ACTIVE, pageable).map(ListingDto::from);
    }
}
