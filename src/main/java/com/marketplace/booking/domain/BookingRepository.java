package com.marketplace.booking.domain;

import com.marketplace.booking.domain.Booking;

import java.util.Optional;
import java.util.UUID;

public interface BookingRepository {
    Booking save(Booking booking);
    Optional<Booking> findById(UUID id);
    java.util.List<Booking> findByConsumerId(UUID consumerId);
    java.util.List<Booking> findByProviderId(UUID providerId);
}
