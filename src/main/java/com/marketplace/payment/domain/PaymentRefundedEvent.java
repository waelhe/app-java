package com.marketplace.payment.domain;

import com.marketplace.shared.domain.DomainEvent;
import com.marketplace.shared.domain.Money;

import java.time.Instant;
import java.util.UUID;

public record PaymentRefundedEvent(
    UUID paymentId,
    UUID bookingId,
    UUID consumerId,
    Money refundAmount
) implements DomainEvent {
    @Override
    public UUID eventId() { return UUID.randomUUID(); }

    @Override
    public Instant occurredAt() { return Instant.now(); }
}
