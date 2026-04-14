package com.marketplace.payment.domain;

import com.marketplace.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record PaymentFrozenEvent(
    UUID paymentId,
    UUID bookingId,
    UUID consumerId,
    UUID providerId
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }

    @Override
    public Instant occurredAt() { return Instant.now(); }
}
