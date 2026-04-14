package com.marketplace.dispute.domain;

import com.marketplace.shared.domain.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public record DisputeResolvedEvent(
    UUID disputeId,
    UUID bookingId,
    UUID complainantId,
    UUID respondentId,
    boolean released
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }

    @Override
    public Instant occurredAt() { return Instant.now(); }
}
