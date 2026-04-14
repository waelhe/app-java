package com.marketplace.listing.domain;

import com.marketplace.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record ListingActivatedEvent(
    UUID listingId,
    UUID providerId,
    String title,
    String category
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }
    @Override
    public Instant occurredAt() { return Instant.now(); }
}
