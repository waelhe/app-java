package com.marketplace.payment.infrastructure;

import com.marketplace.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaPaymentRepository extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByBookingId(UUID bookingId);
    Optional<Payment> findTopByProviderIdAndStatusOrderByCapturedAtDesc(UUID providerId, com.marketplace.payment.domain.PaymentStatus status);
}
