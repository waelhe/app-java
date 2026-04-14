package com.marketplace.booking.domain;

import com.marketplace.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record BookingConfirmedEvent(
    UUID bookingId,
    UUID consumerId,
    UUID providerId,
    UUID listingId
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }

    @Override
    public Instant occurredAt() { return Instant.now(); }
}
