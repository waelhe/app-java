package com.marketplace.user.domain;

import com.marketplace.shared.domain.DomainEvent;

import java.util.UUID;

public record UserRegisteredEvent(
    UUID userId,
    String email,
    String role
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }
}
