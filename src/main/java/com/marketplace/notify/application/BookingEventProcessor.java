package com.marketplace.notify.application;

import com.marketplace.booking.domain.BookingCancelledEvent;
import com.marketplace.booking.domain.BookingConfirmedEvent;
import com.marketplace.notify.domain.NotificationChannel;
import com.marketplace.notify.domain.NotificationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

/**
 * Event processor that listens to booking domain events and sends
 * notifications to both the consumer and the provider:
 * - BookingConfirmedEvent → notify consumer (confirmation) + provider (new booking)
 * - BookingCancelledEvent → notify consumer (cancellation) + provider (booking cancelled)
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BookingEventProcessor {

    private final NotificationService notificationService;

    @ApplicationModuleListener
    public void on(BookingConfirmedEvent event) {
        log.info("Received BookingConfirmedEvent for booking {}", event.bookingId());
        try {
            // Notify consumer: booking confirmed
            notificationService.sendNotification(
                    event.consumerId(),
                    NotificationType.BOOKING_CONFIRMED,
                    NotificationChannel.IN_APP,
                    "Booking Confirmed",
                    "Your booking has been confirmed.",
                    "BOOKING",
                    event.bookingId()
            );

            // Notify provider: new booking to fulfill
            notificationService.sendNotification(
                    event.providerId(),
                    NotificationType.BOOKING_CONFIRMED,
                    NotificationChannel.IN_APP,
                    "New Booking",
                    "You have a new confirmed booking.",
                    "BOOKING",
                    event.bookingId()
            );
        } catch (Exception e) {
            log.error("Failed to send booking confirmed notifications for booking {}: {}",
                    event.bookingId(), e.getMessage(), e);
        }
    }

    @ApplicationModuleListener
    public void on(BookingCancelledEvent event) {
        log.info("Received BookingCancelledEvent for booking {}", event.bookingId());
        try {
            // Notify consumer: booking cancelled
            notificationService.sendNotification(
                    event.consumerId(),
                    NotificationType.BOOKING_CANCELLED,
                    NotificationChannel.IN_APP,
                    "Booking Cancelled",
                    "Your booking has been cancelled.",
                    "BOOKING",
                    event.bookingId()
            );

            // Notify provider: booking cancelled
            notificationService.sendNotification(
                    event.providerId(),
                    NotificationType.BOOKING_CANCELLED,
                    NotificationChannel.IN_APP,
                    "Booking Cancelled",
                    "A booking has been cancelled.",
                    "BOOKING",
                    event.bookingId()
            );
        } catch (Exception e) {
            log.error("Failed to send booking cancelled notifications for booking {}: {}",
                    event.bookingId(), e.getMessage(), e);
        }
    }
}
