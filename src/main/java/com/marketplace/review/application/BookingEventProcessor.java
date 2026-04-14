package com.marketplace.review.application;

import com.marketplace.booking.domain.BookingCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Event processor that listens to booking domain events and enables
 * review functionality for completed bookings.
 *
 * - BookingCompletedEvent: Marks that a review is now available for the
 *   consumer to submit about the provider. In a full implementation, this
 *   would create a review invitation record. For now, it logs the availability.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventProcessor {

    @ApplicationModuleListener
    public void on(BookingCompletedEvent event) {
        log.info("Booking {} completed — review is now available for consumer {} to review provider {}",
                event.bookingId(), event.consumerId(), event.providerId());
        try {
            // In a full implementation, this would:
            // 1. Create a ReviewInvitation entity (bookingId, consumerId, providerId, listingId, expiresAt)
            // 2. Send a notification to the consumer prompting them to leave a review
            // 3. Set a deadline (e.g., 14 days) after which the invitation expires
            //
            // For now, we log that the review window is open.
            log.info("Review invitation implicitly created for bookingId={}, consumerId={}, providerId={}",
                    event.bookingId(), event.consumerId(), event.providerId());
        } catch (Exception e) {
            log.error("Failed to process BookingCompletedEvent for booking {}: {}",
                    event.bookingId(), e.getMessage(), e);
        }
    }
}
