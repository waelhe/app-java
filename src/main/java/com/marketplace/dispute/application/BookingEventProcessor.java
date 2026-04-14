package com.marketplace.dispute.application;

import com.marketplace.booking.domain.BookingDisputedEvent;
import com.marketplace.dispute.domain.Dispute;
import com.marketplace.dispute.domain.DisputeRepository;
import com.marketplace.dispute.domain.DisputeStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Event processor that listens to BookingDisputedEvent from the booking.domain module
 * and auto-creates a dispute when a booking is disputed.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventProcessor {

    private final DisputeRepository disputeRepository;

    @ApplicationModuleListener
    public void on(BookingDisputedEvent event) {
        log.info("Received BookingDisputedEvent for booking {}", event.bookingId());
        try {
            // Only create a dispute if one doesn't already exist for this booking
            disputeRepository.findByBookingId(event.bookingId()).ifPresentOrElse(
                    existing -> log.info("Dispute already exists for booking {}, skipping auto-creation", event.bookingId()),
                    () -> {
                        Dispute dispute = Dispute.builder()
                                .bookingId(event.bookingId())
                                .complainantId(event.consumerId())
                                .respondentId(event.providerId())
                                .reason("Dispute auto-created from booking dispute event")
                                .status(DisputeStatus.OPEN)
                                .build();
                        disputeRepository.save(dispute);
                        log.info("Auto-created dispute for booking {}", event.bookingId());
                    }
            );
        } catch (Exception e) {
            log.error("Failed to auto-create dispute for booking {}: {}", event.bookingId(), e.getMessage(), e);
        }
    }
}
