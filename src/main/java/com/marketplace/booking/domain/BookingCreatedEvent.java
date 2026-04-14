package com.marketplace.booking.domain;

import com.marketplace.shared.domain.DomainEvent;
import com.marketplace.shared.domain.Money;

import java.time.Instant;
import java.util.UUID;

public record BookingCreatedEvent(
    UUID bookingId,
    UUID consumerId,
    UUID providerId,
    Money totalPrice
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }

    @Override
    public Instant occurredAt() { return Instant.now(); }
}
