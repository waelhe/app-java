package com.marketplace.payment.presentation;

import com.marketplace.payment.application.PaymentDto;
import com.marketplace.payment.application.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Payment management")
@NamedInterface("api")
public class PaymentController {

    private final PaymentService paymentService;

    @GetMapping("/{bookingId}")
    @Operation(summary = "Get payment by booking ID")
    public ResponseEntity<PaymentDto> getPayment(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID bookingId
    ) {
        return ResponseEntity.ok(paymentService.getPayment(bookingId));
    }

    @GetMapping("/me/earnings")
    @Operation(summary = "Get provider earnings summary")
    public ResponseEntity<PaymentDto> getProviderEarnings(
            @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(paymentService.getProviderEarnings(UUID.fromString(userId)));
    }

    @PostMapping("/{bookingId}/resolve")
    @Operation(summary = "Resolve a disputed payment")
    public ResponseEntity<PaymentDto> resolvePayment(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID bookingId,
            @RequestParam boolean released
    ) {
        return ResponseEntity.ok(paymentService.resolvePayment(bookingId, released));
    }
}
