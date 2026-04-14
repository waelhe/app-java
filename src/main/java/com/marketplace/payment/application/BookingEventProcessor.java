package com.marketplace.payment.application;

import com.marketplace.booking.domain.BookingCancelledEvent;
import com.marketplace.booking.domain.BookingCompletedEvent;
import com.marketplace.booking.domain.BookingCreatedEvent;
import com.marketplace.booking.domain.BookingDisputedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Event processor that listens to booking domain events and triggers
 * corresponding payment operations using the escrow pattern:
 * - BookingCreatedEvent   → hold payment (escrow hold)
 * - BookingCompletedEvent → capture payment (release to provider)
 * - BookingCancelledEvent → refund payment (return to consumer)
 * - BookingDisputedEvent  → freeze payment (hold during dispute)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventProcessor {

    private final PaymentService paymentService;

    @ApplicationModuleListener
    public void on(BookingCreatedEvent event) {
        log.info("Received BookingCreatedEvent for booking {}", event.bookingId());
        try {
            paymentService.holdPayment(event);
        } catch (Exception e) {
            log.error("Failed to hold payment for booking {}: {}", event.bookingId(), e.getMessage(), e);
        }
    }

    @ApplicationModuleListener
    public void on(BookingCompletedEvent event) {
        log.info("Received BookingCompletedEvent for booking {}", event.bookingId());
        try {
            paymentService.capturePayment(event);
        } catch (Exception e) {
            log.error("Failed to capture payment for booking {}: {}", event.bookingId(), e.getMessage(), e);
        }
    }

    @ApplicationModuleListener
    public void on(BookingCancelledEvent event) {
        log.info("Received BookingCancelledEvent for booking {}", event.bookingId());
        try {
            paymentService.refundPayment(event);
        } catch (Exception e) {
            log.error("Failed to refund payment for booking {}: {}", event.bookingId(), e.getMessage(), e);
        }
    }

    @ApplicationModuleListener
    public void on(BookingDisputedEvent event) {
        log.info("Received BookingDisputedEvent for booking {}", event.bookingId());
        try {
            paymentService.freezePayment(event);
        } catch (Exception e) {
            log.error("Failed to freeze payment for booking {}: {}", event.bookingId(), e.getMessage(), e);
        }
    }
}
