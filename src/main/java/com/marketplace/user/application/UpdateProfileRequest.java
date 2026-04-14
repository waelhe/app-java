package com.marketplace.user.application;

public record UpdateProfileRequest(
    String firstName,
    String lastName,
    String phone,
    String avatarUrl
) {}
