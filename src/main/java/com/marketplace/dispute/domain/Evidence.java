package com.marketplace.dispute.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "evidence")
public class Evidence extends BaseEntity {

    @Column(nullable = false)
    private UUID disputeId;

    @Column(nullable = false)
    private UUID submittedBy;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 500)
    private String attachmentUrl;

    @Column(length = 50)
    private String evidenceType;

    /**
     * Returns the evidence type.
     */
    public String getType() {
        return this.evidenceType;
    }
}
