package com.marketplace.dispute.domain;

import com.marketplace.shared.domain.AggregateRoot;
import com.marketplace.shared.exception.BusinessException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "disputes")
public class Dispute extends AggregateRoot {

    @Column(nullable = false)
    private UUID bookingId;

    @Column(nullable = false)
    private UUID complainantId;

    @Column(nullable = false)
    private UUID respondentId;

    @Column(nullable = false, length = 1000)
    private String reason;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private DisputeStatus status;

    @Column(columnDefinition = "TEXT")
    private String resolution;

    private UUID resolvedBy;

    private Instant resolvedAt;

    private Instant createdAt;

    /**
     * Opens the dispute for review. Transitions from OPEN to UNDER_REVIEW.
     */
    public void open() {
        if (this.status != DisputeStatus.OPEN) {
            throw new BusinessException("Only OPEN disputes can be opened for review");
        }
        this.status = DisputeStatus.UNDER_REVIEW;
    }

    /**
     * Resolves the dispute. Transitions from UNDER_REVIEW (or OPEN) to RESOLVED.
     * If released is true, fires DisputeResolvedEvent with released=true (funds released to provider).
     * If released is false, fires DisputeResolvedEvent with released=false (funds refunded to consumer).
     */
    public void resolve(String resolution, UUID resolvedBy, boolean released) {
        if (this.status != DisputeStatus.UNDER_REVIEW && this.status != DisputeStatus.OPEN && this.status != DisputeStatus.ESCALATED) {
            throw new BusinessException("Only OPEN, UNDER_REVIEW, or ESCALATED disputes can be resolved");
        }
        if (resolution == null || resolution.isBlank()) {
            throw new BusinessException("Resolution cannot be empty");
        }
        this.status = DisputeStatus.RESOLVED;
        this.resolution = resolution;
        this.resolvedBy = resolvedBy;
        this.resolvedAt = Instant.now();
        registerEvent(new DisputeResolvedEvent(
                getId(), bookingId, complainantId, respondentId, released
        ));
    }

    /**
     * Escalates the dispute. Transitions from UNDER_REVIEW to ESCALATED.
     */
    public void escalate() {
        if (this.status != DisputeStatus.UNDER_REVIEW && this.status != DisputeStatus.OPEN) {
            throw new BusinessException("Only OPEN or UNDER_REVIEW disputes can be escalated");
        }
        this.status = DisputeStatus.ESCALATED;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.status == null) {
            this.status = DisputeStatus.OPEN;
        }
    }
}
