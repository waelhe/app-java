package com.marketplace.payment.infrastructure;

import com.marketplace.shared.domain.Money;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Stripe gateway integration stub.
 * In production, this would use Stripe Java SDK to interact with the Stripe API.
 */
@Service
@Slf4j
public class StripeGateway {

    /**
     * Creates a Stripe PaymentIntent with manual capture (hold/authorize).
     * In production: PaymentIntentCreateParams.builder()
     *   .setAmount(cents).setCurrency(currency).setCaptureMethod(CaptureMethod.MANUAL)
     *   .setCustomer(customerId).build()
     *
     * @param amount the amount to hold
     * @return the Stripe PaymentIntent ID
     */
    public String createPaymentIntent(Money amount) {
        // Stub implementation
        String paymentIntentId = "pi_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        log.info("[STRIPE] Created PaymentIntent {} for amount {} {}", paymentIntentId, amount.getAmount(), amount.getCurrency());
        return paymentIntentId;
    }

    /**
     * Captures a previously held PaymentIntent.
     * In production: PaymentIntentCaptureParams.builder().build()
     *
     * @param stripePaymentIntentId the Stripe PaymentIntent ID to capture
     */
    public void capturePaymentIntent(String stripePaymentIntentId) {
        // Stub implementation
        log.info("[STRIPE] Captured PaymentIntent {}", stripePaymentIntentId);
    }

    /**
     * Creates a refund for a PaymentIntent.
     * In production: RefundCreateParams.builder()
     *   .setPaymentIntent(stripePaymentIntentId).setAmount(refundCents).build()
     *
     * @param stripePaymentIntentId the Stripe PaymentIntent ID to refund
     * @param refundAmount the amount to refund
     */
    public void createRefund(String stripePaymentIntentId, Money refundAmount) {
        // Stub implementation
        log.info("[STRIPE] Created refund for PaymentIntent {}, amount {} {}",
                stripePaymentIntentId, refundAmount.getAmount(), refundAmount.getCurrency());
    }

    /**
     * Creates a Stripe Transfer to a connected provider account.
     * In production: TransferCreateParams.builder()
     *   .setAmount(cents).setCurrency(currency)
     *   .setDestination(providerStripeAccountId).build()
     *
     * @param providerId the internal provider ID (mapped to Stripe connected account)
     * @param payoutAmount the amount to transfer to the provider
     * @return the Stripe Transfer ID
     */
    public String createTransfer(UUID providerId, Money payoutAmount) {
        // Stub implementation
        String transferId = "tr_" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        log.info("[STRIPE] Created Transfer {} to provider {} for amount {} {}",
                transferId, providerId, payoutAmount.getAmount(), payoutAmount.getCurrency());
        return transferId;
    }
}
