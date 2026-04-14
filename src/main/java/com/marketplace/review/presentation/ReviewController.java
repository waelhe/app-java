package com.marketplace.review.presentation;

import com.marketplace.review.application.CreateReviewRequest;
import com.marketplace.review.application.ReviewDto;
import com.marketplace.review.application.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
@Tag(name = "Reviews", description = "Review management")
@NamedInterface("api")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    @Operation(summary = "Create a new review")
    public ResponseEntity<ReviewDto> createReview(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateReviewRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(reviewService.createReview(request, UUID.fromString(userId)));
    }

    @PostMapping("/{id}/respond")
    @Operation(summary = "Respond to a review as the provider")
    public ResponseEntity<ReviewDto> respondToReview(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID id,
        @RequestBody String response
    ) {
        return ResponseEntity.ok(reviewService.respondToReview(id, UUID.fromString(userId), response));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all reviews for a user (as reviewee)")
    public ResponseEntity<List<ReviewDto>> getReviewsForUser(
        @PathVariable UUID userId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
    }

    @GetMapping("/listing/{listingId}")
    @Operation(summary = "Get all reviews for a listing")
    public ResponseEntity<List<ReviewDto>> getReviewsForListing(
        @PathVariable UUID listingId
    ) {
        return ResponseEntity.ok(reviewService.getReviewsForListing(listingId));
    }
}
