-- DISPUTE module tables
CREATE TABLE disputes (
    id UUID NOT NULL,
    booking_id UUID NOT NULL,
    complainant_id UUID NOT NULL,
    respondent_id UUID NOT NULL,
    reason VARCHAR(1000) NOT NULL,
    status VARCHAR(50) NOT NULL DEFAULT 'OPEN' CHECK (status IN ('OPEN', 'UNDER_REVIEW', 'RESOLVED', 'ESCALATED', 'CLOSED')),
    resolution TEXT,
    resolved_by UUID,
    resolved_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_disputes PRIMARY KEY (id),
    CONSTRAINT fk_disputes_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_disputes_complainant FOREIGN KEY (complainant_id) REFERENCES users(id),
    CONSTRAINT fk_disputes_respondent FOREIGN KEY (respondent_id) REFERENCES users(id),
    CONSTRAINT uq_disputes_booking UNIQUE (booking_id)
);

CREATE INDEX idx_disputes_complainant ON disputes (complainant_id);
CREATE INDEX idx_disputes_status ON disputes (status);

CREATE TABLE evidence (
    id UUID NOT NULL,
    dispute_id UUID NOT NULL,
    submitted_by UUID NOT NULL,
    description TEXT,
    attachment_url VARCHAR(500),
    evidence_type VARCHAR(50),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_evidence PRIMARY KEY (id),
    CONSTRAINT fk_evidence_dispute FOREIGN KEY (dispute_id) REFERENCES disputes(id) ON DELETE CASCADE,
    CONSTRAINT fk_evidence_submitter FOREIGN KEY (submitted_by) REFERENCES users(id)
);

CREATE INDEX idx_evidence_dispute ON evidence (dispute_id);
