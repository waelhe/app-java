package com.marketplace.booking.application;

import com.marketplace.booking.domain.*;
import com.marketplace.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;

    @Transactional
    public BookingDto createBooking(CreateBookingRequest request, UUID consumerId) {
        if (request.startDate().isAfter(request.endDate())) {
            throw new BusinessException("Start date must be before end date");
        }
        if (request.providerId().equals(consumerId)) {
            throw new BusinessException("Consumer cannot book their own listing");
        }

        Booking booking = Booking.builder()
            .consumerId(consumerId)
            .providerId(request.providerId())
            .listingId(request.listingId())
            .startDate(request.startDate())
            .endDate(request.endDate())
            .totalPrice(request.totalPrice())
            .status(BookingStatus.PENDING)
            .consumerNotes(request.notes())
            .build();

        booking = bookingRepository.save(booking);
        log.info("Booking created: id={}, consumerId={}, providerId={}", booking.getId(), consumerId, request.providerId());
        return BookingDto.from(booking);
    }

    @Transactional(readOnly = true)
    public BookingDto getBooking(UUID bookingId, UUID userId) {
        Booking booking = findBookingOrThrow(bookingId);
        verifyOwnership(booking, userId);
        return BookingDto.from(booking);
    }

    @Transactional
    public BookingDto confirmBooking(UUID bookingId, UUID providerId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (!booking.getProviderId().equals(providerId)) {
            throw new BusinessException("Only the provider can confirm a booking");
        }
        booking.confirm();
        booking = bookingRepository.save(booking);
        log.info("Booking confirmed: id={}", bookingId);
        return BookingDto.from(booking);
    }

    @Transactional
    public BookingDto cancelBooking(UUID bookingId, UUID userId, String reason) {
        Booking booking = findBookingOrThrow(bookingId);
        if (!booking.getConsumerId().equals(userId) && !booking.getProviderId().equals(userId)) {
            throw new BusinessException("Only the consumer or provider can cancel a booking");
        }
        booking.cancel(reason);
        booking = bookingRepository.save(booking);
        log.info("Booking cancelled: id={}, reason={}", bookingId, reason);
        return BookingDto.from(booking);
    }

    @Transactional
    public BookingDto completeBooking(UUID bookingId, UUID providerId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (!booking.getProviderId().equals(providerId)) {
            throw new BusinessException("Only the provider can complete a booking");
        }
        booking.complete();
        booking = bookingRepository.save(booking);
        log.info("Booking completed: id={}", bookingId);
        return BookingDto.from(booking);
    }

    @Transactional
    public BookingDto disputeBooking(UUID bookingId, UUID consumerId) {
        Booking booking = findBookingOrThrow(bookingId);
        if (!booking.getConsumerId().equals(consumerId)) {
            throw new BusinessException("Only the consumer can dispute a booking");
        }
        booking.dispute();
        booking = bookingRepository.save(booking);
        log.info("Booking disputed: id={}", bookingId);
        return BookingDto.from(booking);
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getConsumerBookings(UUID consumerId) {
        return bookingRepository.findByConsumerId(consumerId).stream()
            .map(BookingDto::from)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<BookingDto> getProviderBookings(UUID providerId) {
        return bookingRepository.findByProviderId(providerId).stream()
            .map(BookingDto::from)
            .toList();
    }

    private Booking findBookingOrThrow(UUID bookingId) {
        return bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BusinessException("Booking not found"));
    }

    private void verifyOwnership(Booking booking, UUID userId) {
        if (!booking.getConsumerId().equals(userId) && !booking.getProviderId().equals(userId)) {
            throw new BusinessException("Not authorized to access this booking");
        }
    }
}
