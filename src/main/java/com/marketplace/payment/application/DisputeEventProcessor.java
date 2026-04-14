package com.marketplace.payment.application;

import com.marketplace.dispute.domain.DisputeResolvedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Event processor that listens to dispute domain events and triggers
 * corresponding payment operations:
 * - DisputeResolvedEvent (released=true)  → release payment to provider
 * - DisputeResolvedEvent (released=false) → refund payment to consumer
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DisputeEventProcessor {

    private final PaymentService paymentService;

    @ApplicationModuleListener
    public void on(DisputeResolvedEvent event) {
        log.info("Received DisputeResolvedEvent for dispute {}, booking {}, released={}",
                event.disputeId(), event.bookingId(), event.released());
        try {
            paymentService.resolvePayment(event.bookingId(), event.released());
        } catch (Exception e) {
            log.error("Failed to resolve payment for booking {} from dispute {}: {}",
                    event.bookingId(), event.disputeId(), e.getMessage(), e);
        }
    }
}