package com.marketplace.dispute.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddEvidenceRequest(
    @NotBlank @Size(max = 10000) String description,
    @Size(max = 500) String attachmentUrl,
    @Size(max = 50) String evidenceType
) {}
