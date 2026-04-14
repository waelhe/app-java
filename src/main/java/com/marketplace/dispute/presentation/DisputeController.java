package com.marketplace.dispute.presentation;

import com.marketplace.dispute.application.AddEvidenceRequest;
import com.marketplace.dispute.application.CreateDisputeRequest;
import com.marketplace.dispute.application.DisputeDto;
import com.marketplace.dispute.application.DisputeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/disputes")
@RequiredArgsConstructor
@Tag(name = "Disputes", description = "Dispute management")
@NamedInterface("api")
public class DisputeController {

    private final DisputeService disputeService;

    @PostMapping
    @Operation(summary = "Create a new dispute")
    public ResponseEntity<DisputeDto> createDispute(
            @AuthenticationPrincipal String userId,
            @Valid @RequestBody CreateDisputeRequest request
    ) {
        DisputeDto dispute = disputeService.createDispute(
                request.bookingId(),
                UUID.fromString(userId),
                request.respondentId(),
                request.reason()
        );
        return ResponseEntity.ok(dispute);
    }

    @PostMapping("/{id}/evidence")
    @Operation(summary = "Add evidence to a dispute")
    public ResponseEntity<Void> addEvidence(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @Valid @RequestBody AddEvidenceRequest request
    ) {
        disputeService.addEvidence(
                id,
                UUID.fromString(userId),
                request.description(),
                request.attachmentUrl(),
                request.evidenceType()
        );
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/resolve")
    @Operation(summary = "Resolve a dispute")
    public ResponseEntity<DisputeDto> resolveDispute(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id,
            @RequestParam String resolution,
            @RequestParam boolean released
    ) {
        DisputeDto dispute = disputeService.resolveDispute(
                id,
                UUID.fromString(userId),
                resolution,
                released
        );
        return ResponseEntity.ok(dispute);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get dispute by ID")
    public ResponseEntity<DisputeDto> getDispute(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id
    ) {
        return ResponseEntity.ok(disputeService.getDispute(id));
    }

    @GetMapping("/user")
    @Operation(summary = "Get disputes for the authenticated user")
    public ResponseEntity<Page<DisputeDto>> getDisputesByUser(
            @AuthenticationPrincipal String userId,
            Pageable pageable
    ) {
        return ResponseEntity.ok(disputeService.getDisputesByUser(UUID.fromString(userId), pageable));
    }
}
