package com.marketplace.user.presentation;

import com.marketplace.user.application.UpdateProfileRequest;
import com.marketplace.user.application.UserDto;
import com.marketplace.user.application.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User profile management")
@NamedInterface("api")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal String userId) {
        return ResponseEntity.ok(userService.getUser(UUID.fromString(userId)));
    }

    @GetMapping("/{userId}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<UserDto> getUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUser(userId));
    }

    @PutMapping("/me")
    @Operation(summary = "Update current user profile")
    public ResponseEntity<UserDto> updateProfile(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody UpdateProfileRequest request
    ) {
        return ResponseEntity.ok(userService.updateProfile(UUID.fromString(userId), request));
    }

    @PostMapping("/me/verify-email")
    @Operation(summary = "Verify email address")
    public ResponseEntity<Void> verifyEmail(@AuthenticationPrincipal String userId) {
        userService.verifyEmail(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/me/verify-phone")
    @Operation(summary = "Verify phone number")
    public ResponseEntity<Void> verifyPhone(@AuthenticationPrincipal String userId) {
        userService.verifyPhone(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }
}
