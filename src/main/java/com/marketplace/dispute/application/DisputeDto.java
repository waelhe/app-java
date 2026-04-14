package com.marketplace.dispute.application;

import com.marketplace.dispute.domain.Dispute;
import com.marketplace.dispute.domain.DisputeStatus;

import java.time.Instant;
import java.util.UUID;

public record DisputeDto(
    UUID id,
    UUID bookingId,
    UUID complainantId,
    UUID respondentId,
    String reason,
    DisputeStatus status,
    String resolution,
    UUID resolvedBy,
    Instant resolvedAt,
    Instant createdAt
) {
    public static DisputeDto from(Dispute dispute) {
        return new DisputeDto(
                dispute.getId(),
                dispute.getBookingId(),
                dispute.getComplainantId(),
                dispute.getRespondentId(),
                dispute.getReason(),
                dispute.getStatus(),
                dispute.getResolution(),
                dispute.getResolvedBy(),
                dispute.getResolvedAt(),
                dispute.getCreatedAt()
        );
    }
}
