package com.marketplace.listing.presentation;

import com.marketplace.listing.application.CreateListingRequest;
import com.marketplace.listing.application.ListingDto;
import com.marketplace.listing.application.ListingService;
import com.marketplace.listing.application.UpdateListingRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/listings")
@RequiredArgsConstructor
@Tag(name = "Listings", description = "Listing management")
@NamedInterface("api")
public class ListingController {

    private final ListingService listingService;

    @PostMapping
    @Operation(summary = "Create a new listing")
    public ResponseEntity<ListingDto> createListing(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateListingRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(listingService.createListing(request, UUID.fromString(userId)));
    }

    @GetMapping("/{listingId}")
    @Operation(summary = "Get listing by ID")
    public ResponseEntity<ListingDto> getListing(@PathVariable UUID listingId) {
        return ResponseEntity.ok(listingService.getListing(listingId));
    }

    @PutMapping("/{listingId}")
    @Operation(summary = "Update listing")
    public ResponseEntity<ListingDto> updateListing(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID listingId,
        @Valid @RequestBody UpdateListingRequest request
    ) {
        return ResponseEntity.ok(listingService.updateListing(listingId, UUID.fromString(userId), request));
    }

    @PostMapping("/{listingId}/activate")
    @Operation(summary = "Activate listing")
    public ResponseEntity<Void> activateListing(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID listingId
    ) {
        listingService.activateListing(listingId, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{listingId}/deactivate")
    @Operation(summary = "Deactivate listing")
    public ResponseEntity<Void> deactivateListing(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID listingId
    ) {
        listingService.deactivateListing(listingId, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/provider")
    @Operation(summary = "Get provider's listings")
    public ResponseEntity<Page<ListingDto>> getProviderListings(
        @AuthenticationPrincipal String userId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(listingService.getProviderListings(UUID.fromString(userId), pageable));
    }

    @GetMapping
    @Operation(summary = "Search listings")
    public ResponseEntity<Page<ListingDto>> searchListings(
        @RequestParam(required = false) String category,
        Pageable pageable
    ) {
        return ResponseEntity.ok(listingService.searchListings(category, pageable));
    }
}
