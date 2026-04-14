package com.marketplace.notify.application;

import com.marketplace.payment.domain.PaymentCapturedEvent;
import com.marketplace.payment.domain.PaymentRefundedEvent;
import com.marketplace.notify.domain.NotificationChannel;
import com.marketplace.notify.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Event processor that listens to payment domain events and sends
 * notifications to the relevant parties:
 * - PaymentCapturedEvent → notify provider (payment received / payout)
 * - PaymentRefundedEvent → notify consumer (refund processed)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventProcessor {

    private final NotificationService notificationService;

    @ApplicationModuleListener
    public void on(PaymentCapturedEvent event) {
        log.info("Received PaymentCapturedEvent for payment {}", event.paymentId());
        try {
            // Notify provider: payment received / payout processed
            notificationService.sendNotification(
                    event.providerId(),
                    NotificationType.PAYMENT_RECEIVED,
                    NotificationChannel.IN_APP,
                    "Payment Received",
                    "A payment has been captured and your payout is being processed.",
                    "PAYMENT",
                    event.paymentId()
            );
        } catch (Exception e) {
            log.error("Failed to send payment captured notification for payment {}: {}",
                    event.paymentId(), e.getMessage(), e);
        }
    }

    @ApplicationModuleListener
    public void on(PaymentRefundedEvent event) {
        log.info("Received PaymentRefundedEvent for payment {}", event.paymentId());
        try {
            // Notify consumer: refund processed
            notificationService.sendNotification(
                    event.consumerId(),
                    NotificationType.PAYMENT_REFUNDED,
                    NotificationChannel.IN_APP,
                    "Payment Refunded",
                    "Your payment has been refunded.",
                    "PAYMENT",
                    event.paymentId()
            );
        } catch (Exception e) {
            log.error("Failed to send payment refunded notification for payment {}: {}",
                    event.paymentId(), e.getMessage(), e);
        }
    }
}
