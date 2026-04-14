package com.marketplace.review.domain;

import com.marketplace.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ReviewCreatedEvent(
    UUID reviewId,
    UUID revieweeId,
    int rating
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }

    @Override
    public Instant occurredAt() { return Instant.now(); }
}
