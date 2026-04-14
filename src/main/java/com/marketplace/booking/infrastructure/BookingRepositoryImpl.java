package com.marketplace.booking.infrastructure;

import com.marketplace.booking.domain.Booking;
import com.marketplace.booking.domain.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class BookingRepositoryImpl implements BookingRepository {

    private final JpaBookingRepository jpaRepository;

    @Override
    public Booking save(Booking booking) {
        return jpaRepository.save(booking);
    }

    @Override
    public Optional<Booking> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Booking> findByConsumerId(UUID consumerId) {
        return jpaRepository.findByConsumerId(consumerId);
    }

    @Override
    public List<Booking> findByProviderId(UUID providerId) {
        return jpaRepository.findByProviderId(providerId);
    }
}
