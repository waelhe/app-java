package com.marketplace.dispute.infrastructure;

import com.marketplace.dispute.domain.Dispute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface JpaDisputeRepository extends JpaRepository<Dispute, UUID> {
    Optional<Dispute> findByBookingId(UUID bookingId);
    java.util.List<Dispute> findByComplainantId(UUID complainantId);
}
