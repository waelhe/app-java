package com.marketplace.booking.domain;

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
@Table(name = "bookings")
public class Booking extends AggregateRoot {

    @Column(nullable = false)
    private UUID consumerId;

    @Column(nullable = false)
    private UUID providerId;

    @Column(nullable = false)
    private UUID listingId;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Embedded
    private Money totalPrice;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(columnDefinition = "TEXT")
    private String consumerNotes;

    @Column(columnDefinition = "TEXT")
    private String cancellationReason;

    private Instant confirmedAt;

    private Instant cancelledAt;

    private Instant completedAt;

    public void confirm() {
        if (this.status != BookingStatus.PENDING) {
            throw new BusinessException("Only PENDING bookings can be confirmed");
        }
        this.status = BookingStatus.CONFIRMED;
        this.confirmedAt = Instant.now();
        registerEvent(new BookingConfirmedEvent(getId(), consumerId, providerId, listingId));
    }

    public void cancel(String reason) {
        if (this.status != BookingStatus.PENDING && this.status != BookingStatus.CONFIRMED) {
            throw new BusinessException("Only PENDING or CONFIRMED bookings can be cancelled");
        }
        this.status = BookingStatus.CANCELLED;
        this.cancellationReason = reason;
        this.cancelledAt = Instant.now();
        registerEvent(new BookingCancelledEvent(getId(), consumerId, providerId, totalPrice));
    }

    public void complete() {
        if (this.status != BookingStatus.CONFIRMED) {
            throw new BusinessException("Only CONFIRMED bookings can be completed");
        }
        this.status = BookingStatus.COMPLETED;
        this.completedAt = Instant.now();
        registerEvent(new BookingCompletedEvent(getId(), consumerId, providerId, totalPrice));
    }

    public void dispute() {
        if (this.status != BookingStatus.CONFIRMED && this.status != BookingStatus.COMPLETED) {
            throw new BusinessException("Only CONFIRMED or COMPLETED bookings can be disputed");
        }
        this.status = BookingStatus.DISPUTED;
        registerEvent(new BookingDisputedEvent(getId(), consumerId, providerId));
    }
}
