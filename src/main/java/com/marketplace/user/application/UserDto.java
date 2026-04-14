package com.marketplace.user.application;

import com.marketplace.user.domain.User;
import com.marketplace.user.domain.UserRole;
import com.marketplace.user.domain.UserStatus;

import java.time.Instant;
import java.util.UUID;

public record UserDto(
    UUID id,
    String email,
    String firstName,
    String lastName,
    String phone,
    String avatarUrl,
    UserRole role,
    UserStatus status,
    Instant emailVerifiedAt,
    Instant phoneVerifiedAt,
    Instant createdAt
) {
    public static UserDto from(User user) {
        return new UserDto(
            user.getId(),
            user.getEmail(),
            user.getFirstName(),
            user.getLastName(),
            user.getPhone(),
            user.getAvatarUrl(),
            user.getRole(),
            user.getStatus(),
            user.getEmailVerifiedAt(),
            user.getPhoneVerifiedAt(),
            user.getCreatedAt()
        );
    }
}
