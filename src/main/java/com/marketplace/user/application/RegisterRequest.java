package com.marketplace.user.application;

import com.marketplace.user.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    String phone,
    UserRole role
) {
    public RegisterRequest {
        if (role == null) role = UserRole.CONSUMER;
    }
}
