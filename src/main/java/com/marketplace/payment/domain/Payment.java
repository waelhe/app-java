package com.marketplace.payment.domain;

import com.marketplace.shared.domain.AggregateRoot;
import com.marketplace.shared.domain.Money;
import com.marketplace.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "payments")
public class Payment extends AggregateRoot {

    @Column(nullable = false, unique = true)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID consumerId;

    @Column(nullable = false)
    private UUID providerId;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false)),
        @AttributeOverride(name = "currency", column = @Column(name = "amount_currency", nullable = false))
    })
    private Money amount;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "platform_fee_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "platform_fee_currency"))
    })
    private Money platformFee;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "amount", column = @Column(name = "provider_payout_amount")),
        @AttributeOverride(name = "currency", column = @Column(name = "provider_payout_currency"))
    })
    private Money providerPayout;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private String stripePaymentIntentId;

    private String stripeTransferId;

    private Instant holdAt;

    private Instant capturedAt;

    private Instant refundedAt;

    /**
     * Places a hold on the payment (escrow). Transitions from PENDING_HOLD to HELD.
     */
    public void hold(String stripePaymentIntentId) {
        if (this.status != PaymentStatus.PENDING_HOLD) {
            throw new BusinessException("Only PENDING_HOLD payments can be held");
        }
        this.status = PaymentStatus.HELD;
        this.stripePaymentIntentId = stripePaymentIntentId;
        this.holdAt = Instant.now();
        registerEvent(new PaymentHeldEvent(getId(), bookingId, consumerId, providerId, amount));
    }

    /**
     * Captures the held payment (release to provider). Transitions from HELD to CAPTURED.
     * Fires PaymentCapturedEvent with provider payout details.
     */
    public void capture(String stripeTransferId) {
        if (this.status != PaymentStatus.HELD) {
            throw new BusinessException("Only HELD payments can be captured");
        }
        this.status = PaymentStatus.CAPTURED;
        this.stripeTransferId = stripeTransferId;
        this.capturedAt = Instant.now();
        registerEvent(new PaymentCapturedEvent(getId(), bookingId, providerId, providerPayout));
    }

    /**
     * Refunds the held payment back to consumer. Transitions from HELD to REFUNDED.
     */
    public void refund() {
        if (this.status != PaymentStatus.HELD) {
            throw new BusinessException("Only HELD payments can be refunded");
        }
        this.status = PaymentStatus.REFUNDED;
        this.refundedAt = Instant.now();
        registerEvent(new PaymentRefundedEvent(getId(), bookingId, consumerId, amount));
    }

    /**
     * Freezes the payment due to a dispute. Transitions from HELD or CAPTURED to FROZEN.
     */
    public void freeze() {
        if (this.status != PaymentStatus.HELD && this.status != PaymentStatus.CAPTURED) {
            throw new BusinessException("Only HELD or CAPTURED payments can be frozen");
        }
        this.status = PaymentStatus.FROZEN;
        registerEvent(new PaymentFrozenEvent(getId(), bookingId, consumerId, providerId));
    }

    /**
     * Unfreezes a frozen payment — either releases to provider or refunds to consumer.
     */
    public void unfreeze(boolean releaseToProvider, String stripeTransferId) {
        if (this.status != PaymentStatus.FROZEN) {
            throw new BusinessException("Only FROZEN payments can be unfrozen");
        }
        if (releaseToProvider) {
            this.status = PaymentStatus.CAPTURED;
            this.stripeTransferId = stripeTransferId;
            this.capturedAt = Instant.now();
            registerEvent(new PaymentCapturedEvent(getId(), bookingId, providerId, providerPayout));
        } else {
            this.status = PaymentStatus.REFUNDED;
            this.refundedAt = Instant.now();
            registerEvent(new PaymentRefundedEvent(getId(), bookingId, consumerId, amount));
        }
    }
}
