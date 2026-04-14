package com.marketplace.payment.domain;

public enum PaymentStatus {
    PENDING_HOLD,
    HELD,
    CAPTURED,
    REFUNDED,
    FROZEN
}
