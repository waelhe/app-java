package com.marketplace.booking.presentation;

import com.marketplace.booking.application.BookingDto;
import com.marketplace.booking.application.BookingService;
import com.marketplace.booking.application.CreateBookingRequest;
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
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
@Tag(name = "Bookings", description = "Booking management")
@NamedInterface("api")
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking")
    public ResponseEntity<BookingDto> createBooking(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody CreateBookingRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(bookingService.createBooking(request, UUID.fromString(userId)));
    }

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get booking by ID")
    public ResponseEntity<BookingDto> getBooking(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID bookingId
    ) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId, UUID.fromString(userId)));
    }

    @PostMapping("/{bookingId}/confirm")
    @Operation(summary = "Confirm a booking")
    public ResponseEntity<BookingDto> confirmBooking(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID bookingId
    ) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId, UUID.fromString(userId)));
    }

    @PostMapping("/{bookingId}/cancel")
    @Operation(summary = "Cancel a booking")
    public ResponseEntity<BookingDto> cancelBooking(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID bookingId,
        @RequestParam(required = false) String reason
    ) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, UUID.fromString(userId), reason));
    }

    @PostMapping("/{bookingId}/complete")
    @Operation(summary = "Complete a booking")
    public ResponseEntity<BookingDto> completeBooking(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID bookingId
    ) {
        return ResponseEntity.ok(bookingService.completeBooking(bookingId, UUID.fromString(userId)));
    }

    @PostMapping("/{bookingId}/dispute")
    @Operation(summary = "Dispute a booking")
    public ResponseEntity<BookingDto> disputeBooking(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID bookingId
    ) {
        return ResponseEntity.ok(bookingService.disputeBooking(bookingId, UUID.fromString(userId)));
    }

    @GetMapping("/consumer")
    @Operation(summary = "Get consumer's bookings")
    public ResponseEntity<List<BookingDto>> getConsumerBookings(
        @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(bookingService.getConsumerBookings(UUID.fromString(userId)));
    }

    @GetMapping("/provider")
    @Operation(summary = "Get provider's bookings")
    public ResponseEntity<List<BookingDto>> getProviderBookings(
        @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(bookingService.getProviderBookings(UUID.fromString(userId)));
    }
}
