package com.marketplace.payment.application;

import com.marketplace.payment.domain.Payment;
import com.marketplace.payment.domain.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentDto(
    UUID id,
    UUID bookingId,
    UUID consumerId,
    UUID providerId,
    BigDecimal amount,
    String currency,
    BigDecimal platformFee,
    BigDecimal providerPayout,
    PaymentStatus status,
    String stripePaymentIntentId,
    String stripeTransferId,
    Instant holdAt,
    Instant capturedAt,
    Instant refundedAt,
    Instant createdAt
) {
    public static PaymentDto from(Payment payment) {
        return new PaymentDto(
            payment.getId(),
            payment.getBookingId(),
            payment.getConsumerId(),
            payment.getProviderId(),
            payment.getAmount() != null ? payment.getAmount().getAmount() : null,
            payment.getAmount() != null ? payment.getAmount().getCurrency() : null,
            payment.getPlatformFee() != null ? payment.getPlatformFee().getAmount() : null,
            payment.getProviderPayout() != null ? payment.getProviderPayout().getAmount() : null,
            payment.getStatus(),
            payment.getStripePaymentIntentId(),
            payment.getStripeTransferId(),
            payment.getHoldAt(),
            payment.getCapturedAt(),
            payment.getRefundedAt(),
            payment.getCreatedAt()
        );
    }
}
