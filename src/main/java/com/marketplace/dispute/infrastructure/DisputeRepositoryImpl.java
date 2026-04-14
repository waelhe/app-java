package com.marketplace.dispute.infrastructure;

import com.marketplace.dispute.domain.Dispute;
import com.marketplace.dispute.domain.DisputeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DisputeRepositoryImpl implements DisputeRepository {

    private final JpaDisputeRepository jpaRepository;

    @Override
    public Dispute save(Dispute dispute) {
        return jpaRepository.save(dispute);
    }

    @Override
    public Optional<Dispute> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public Optional<Dispute> findByBookingId(UUID bookingId) {
        return jpaRepository.findByBookingId(bookingId);
    }

    @Override
    public List<Dispute> findByComplainantId(UUID complainantId) {
        return jpaRepository.findByComplainantId(complainantId);
    }
}
