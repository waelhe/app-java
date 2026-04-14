package com.marketplace.booking.infrastructure;

import com.marketplace.booking.domain.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

interface JpaBookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findByConsumerId(UUID consumerId);
    List<Booking> findByProviderId(UUID providerId);
}
