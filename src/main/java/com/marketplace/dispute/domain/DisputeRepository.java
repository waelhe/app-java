package com.marketplace.dispute.domain;

import java.util.Optional;
import java.util.UUID;

public interface DisputeRepository {
    Dispute save(Dispute dispute);
    Optional<Dispute> findById(UUID id);
    Optional<Dispute> findByBookingId(UUID bookingId);
    java.util.List<Dispute> findByComplainantId(UUID complainantId);
}
