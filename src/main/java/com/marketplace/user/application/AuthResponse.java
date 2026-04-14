package com.marketplace.user.application;

import java.util.UUID;

public record AuthResponse(
    String accessToken,
    String refreshToken,
    UUID userId,
    String email,
    String role
) {}
