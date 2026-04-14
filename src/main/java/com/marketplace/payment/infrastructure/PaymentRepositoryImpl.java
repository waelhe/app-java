package com.marketplace.payment.infrastructure;

import com.marketplace.payment.domain.Payment;
import com.marketplace.payment.domain.PaymentRepository;
import com.marketplace.payment.domain.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final JpaPaymentRepository jpaRepository;

    @Override
    public Payment save(Payment payment) {
        return jpaRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Payment> findByBookingId(UUID bookingId) {
        return jpaRepository.findByBookingId(bookingId);
    }

    @Override
    public Optional<Payment> findByProviderId(UUID providerId) {
        return jpaRepository.findTopByProviderIdAndStatusOrderByCapturedAtDesc(providerId, PaymentStatus.CAPTURED);
    }
}
