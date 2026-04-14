package com.marketplace.dispute.application;

import com.marketplace.dispute.domain.Dispute;
import com.marketplace.dispute.domain.DisputeRepository;
import com.marketplace.dispute.domain.DisputeStatus;
import com.marketplace.dispute.domain.Evidence;
import com.marketplace.shared.exception.BusinessException;
import com.marketplace.dispute.infrastructure.JpaEvidenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DisputeService {

    private final DisputeRepository disputeRepository;
    private final JpaEvidenceRepository evidenceRepository;

    /**
     * Creates a new dispute for a booking.
     * Validates that the complainant is part of the booking by ensuring
     * the complainant is not the same as the respondent (basic validation).
     * In a full implementation, booking participation would be verified
     * via the Booking module (e.g., through a BookingClient).
     */
    @Transactional
    public DisputeDto createDispute(UUID bookingId, UUID complainantId, UUID respondentId, String reason) {
        if (complainantId.equals(respondentId)) {
            throw new BusinessException("Complainant and respondent cannot be the same user");
        }
        if (reason == null || reason.isBlank()) {
            throw new BusinessException("Dispute reason cannot be empty");
        }

        // Check if a dispute already exists for this booking
        disputeRepository.findByBookingId(bookingId).ifPresent(existing -> {
            throw new BusinessException("A dispute already exists for booking " + bookingId);
        });

        Dispute dispute = Dispute.builder()
                .bookingId(bookingId)
                .complainantId(complainantId)
                .respondentId(respondentId)
                .reason(reason)
                .status(DisputeStatus.OPEN)
                .build();

        Dispute saved = disputeRepository.save(dispute);
        log.info("Dispute {} created for booking {} by complainant {}", saved.getId(), bookingId, complainantId);
        return DisputeDto.from(saved);
    }

    /**
     * Adds evidence to an existing dispute.
     */
    @Transactional
    public void addEvidence(UUID disputeId, UUID submittedBy, String description, String attachmentUrl, String type) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new BusinessException("Dispute not found: " + disputeId));

        if (dispute.getStatus() == DisputeStatus.RESOLVED || dispute.getStatus() == DisputeStatus.CLOSED) {
            throw new BusinessException("Cannot add evidence to a resolved or closed dispute");
        }

        Evidence evidence = Evidence.builder()
                .disputeId(disputeId)
                .submittedBy(submittedBy)
                .description(description)
                .attachmentUrl(attachmentUrl)
                .evidenceType(type)
                .build();

        evidenceRepository.save(evidence);
        log.info("Evidence added to dispute {} by user {}", disputeId, submittedBy);
    }

    /**
     * Resolves a dispute with a resolution decision.
     * If released is true, funds are released to the provider (DisputeResolvedEvent.released=true).
     * If released is false, funds are refunded to the consumer (DisputeResolvedEvent.released=false).
     */
    @Transactional
    public DisputeDto resolveDispute(UUID disputeId, UUID resolvedBy, String resolution, boolean released) {
        Dispute dispute = disputeRepository.findById(disputeId)
                .orElseThrow(() -> new BusinessException("Dispute not found: " + disputeId));

        dispute.resolve(resolution, resolvedBy, released);
        Dispute saved = disputeRepository.save(dispute);
        log.info("Dispute {} resolved by {} (released={})", disputeId, resolvedBy, released);
        return DisputeDto.from(saved);
    }

    /**
     * Retrieves a dispute by ID.
     */
    @Transactional(readOnly = true)
    public DisputeDto getDispute(UUID id) {
        Dispute dispute = disputeRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Dispute not found: " + id));
        return DisputeDto.from(dispute);
    }

    /**
     * Retrieves disputes for a user (as complainant).
     */
    @Transactional(readOnly = true)
    public Page<DisputeDto> getDisputesByUser(UUID userId, Pageable pageable) {
        List<Dispute> disputes = disputeRepository.findByComplainantId(userId);
        List<DisputeDto> dtos = disputes.stream()
                .map(DisputeDto::from)
                .toList();
        return new PageImpl<>(dtos, pageable, dtos.size());
    }
}
