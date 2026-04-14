package com.marketplace.payment.application;

import com.marketplace.booking.domain.BookingCancelledEvent;
import com.marketplace.booking.domain.BookingCompletedEvent;
import com.marketplace.booking.domain.BookingCreatedEvent;
import com.marketplace.booking.domain.BookingDisputedEvent;
import com.marketplace.payment.domain.Payment;
import com.marketplace.payment.domain.PaymentRepository;
import com.marketplace.payment.domain.PaymentStatus;
import com.marketplace.payment.infrastructure.StripeGateway;
import com.marketplace.shared.domain.Money;
import com.marketplace.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final StripeGateway stripeGateway;

    /**
     * Platform fee percentage (15%).
     */
    private static final BigDecimal PLATFORM_FEE_PERCENTAGE = new BigDecimal("0.15");

    /**
     * Creates a payment in PENDING_HOLD state and places a hold via Stripe.
     * Triggered by BookingCreatedEvent.
     */
    @Transactional
    public PaymentDto holdPayment(BookingCreatedEvent event) {
        log.info("Holding payment for booking {}", event.bookingId());

        Money amount = event.totalPrice();
        Money platformFee = amount.multiply(PLATFORM_FEE_PERCENTAGE);
        Money providerPayout = amount.subtract(platformFee);

        Payment payment = Payment.builder()
                .bookingId(event.bookingId())
                .consumerId(event.consumerId())
                .providerId(event.providerId())
                .amount(amount)
                .platformFee(platformFee)
                .providerPayout(providerPayout)
                .status(PaymentStatus.PENDING_HOLD)
                .build();

        // Create Stripe payment intent and hold funds
        String stripePaymentIntentId = stripeGateway.createPaymentIntent(amount);
        payment.hold(stripePaymentIntentId);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} held for booking {}, amount={}", saved.getId(), event.bookingId(), amount);
        return PaymentDto.from(saved);
    }

    /**
     * Captures the held payment and initiates provider payout via Stripe transfer.
     * Triggered by BookingCompletedEvent.
     */
    @Transactional
    public PaymentDto capturePayment(BookingCompletedEvent event) {
        log.info("Capturing payment for booking {}", event.bookingId());

        Payment payment = paymentRepository.findByBookingId(event.bookingId())
                .orElseThrow(() -> new BusinessException("Payment not found for booking: " + event.bookingId()));

        // Capture payment intent via Stripe
        stripeGateway.capturePaymentIntent(payment.getStripePaymentIntentId());

        // Create transfer to provider via Stripe
        String stripeTransferId = stripeGateway.createTransfer(
                payment.getProviderId(),
                payment.getProviderPayout()
        );

        payment.capture(stripeTransferId);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} captured for booking {}, provider payout={}", saved.getId(), event.bookingId(), payment.getProviderPayout());
        return PaymentDto.from(saved);
    }

    /**
     * Refunds the held payment back to the consumer.
     * Triggered by BookingCancelledEvent.
     */
    @Transactional
    public PaymentDto refundPayment(BookingCancelledEvent event) {
        log.info("Refunding payment for booking {}", event.bookingId());

        Payment payment = paymentRepository.findByBookingId(event.bookingId())
                .orElseThrow(() -> new BusinessException("Payment not found for booking: " + event.bookingId()));

        // Create refund via Stripe
        stripeGateway.createRefund(payment.getStripePaymentIntentId(), payment.getAmount());

        payment.refund();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} refunded for booking {}, refund amount={}", saved.getId(), event.bookingId(), payment.getAmount());
        return PaymentDto.from(saved);
    }

    /**
     * Freezes the payment due to a dispute.
     * Triggered by BookingDisputedEvent.
     */
    @Transactional
    public PaymentDto freezePayment(BookingDisputedEvent event) {
        log.info("Freezing payment for booking {}", event.bookingId());

        Payment payment = paymentRepository.findByBookingId(event.bookingId())
                .orElseThrow(() -> new BusinessException("Payment not found for booking: " + event.bookingId()));

        payment.freeze();

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} frozen for booking {}", saved.getId(), event.bookingId());
        return PaymentDto.from(saved);
    }

    /**
     * Resolves a dispute — either releases payment to provider or refunds to consumer.
     *
     * @param bookingId the booking ID whose payment is disputed
     * @param released  if true, release to provider; if false, refund to consumer
     */
    @Transactional
    public PaymentDto resolvePayment(UUID bookingId, boolean released) {
        log.info("Resolving payment for booking {}, released={}", bookingId, released);

        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BusinessException("Payment not found for booking: " + bookingId));

        String stripeTransferId = null;
        if (released) {
            // If previously captured, we need to reverse and re-transfer
            // For simplicity, create a new transfer
            stripeTransferId = stripeGateway.createTransfer(
                    payment.getProviderId(),
                    payment.getProviderPayout()
            );
        } else {
            // Refund via Stripe
            stripeGateway.createRefund(payment.getStripePaymentIntentId(), payment.getAmount());
        }

        payment.unfreeze(released, stripeTransferId);

        Payment saved = paymentRepository.save(payment);
        log.info("Payment {} resolved for booking {}, released={}", saved.getId(), bookingId, released);
        return PaymentDto.from(saved);
    }

    /**
     * Retrieves payment details by booking ID.
     */
    @Transactional(readOnly = true)
    public PaymentDto getPayment(UUID bookingId) {
        Payment payment = paymentRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BusinessException("Payment not found for booking: " + bookingId));
        return PaymentDto.from(payment);
    }

    /**
     * Retrieves payment details by provider ID for earnings.
     */
    @Transactional(readOnly = true)
    public PaymentDto getProviderEarnings(UUID providerId) {
        // This is a simplified version; a real implementation would aggregate earnings
        Payment payment = paymentRepository.findByProviderId(providerId)
                .orElseThrow(() -> new BusinessException("No captured payments found for provider: " + providerId));
        return PaymentDto.from(payment);
    }
}
