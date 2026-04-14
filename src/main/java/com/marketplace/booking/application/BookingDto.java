package com.marketplace.booking.application;

import com.marketplace.booking.domain.Booking;
import com.marketplace.booking.domain.BookingStatus;
import com.marketplace.shared.domain.Money;

import java.time.Instant;
import java.util.UUID;

public record BookingDto(
    UUID id,
    UUID consumerId,
    UUID providerId,
    UUID listingId,
    Instant startDate,
    Instant endDate,
    Money totalPrice,
    BookingStatus status,
    String consumerNotes,
    String cancellationReason,
    Instant confirmedAt,
    Instant cancelledAt,
    Instant completedAt,
    Instant createdAt
) {
    public static BookingDto from(Booking booking) {
        return new BookingDto(
            booking.getId(),
            booking.getConsumerId(),
            booking.getProviderId(),
            booking.getListingId(),
            booking.getStartDate(),
            booking.getEndDate(),
            booking.getTotalPrice(),
            booking.getStatus(),
            booking.getConsumerNotes(),
            booking.getCancellationReason(),
            booking.getConfirmedAt(),
            booking.getCancelledAt(),
            booking.getCompletedAt(),
            booking.getCreatedAt()
        );
    }
}
