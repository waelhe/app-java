package com.marketplace.booking.application;

import com.marketplace.shared.domain.Money;

import java.time.Instant;
import java.util.UUID;

public record CreateBookingRequest(
    UUID providerId,
    UUID listingId,
    Instant startDate,
    Instant endDate,
    Money totalPrice,
    String notes
) {}
