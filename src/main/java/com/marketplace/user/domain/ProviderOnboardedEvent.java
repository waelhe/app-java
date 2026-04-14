package com.marketplace.user.domain;

import com.marketplace.shared.domain.DomainEvent;

import java.util.UUID;

public record ProviderOnboardedEvent(
    UUID providerId,
    String businessName,
    String category
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }
}
