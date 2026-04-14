package com.marketplace.user.domain;

import com.marketplace.shared.domain.AggregateRoot;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users", uniqueConstraints = {
    @UniqueConstraint(name = "uq_users_email", columnNames = "email")
})
public class User extends AggregateRoot {

    @Column(nullable = false, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(length = 100)
    private String firstName;

    @Column(length = 100)
    private String lastName;

    @Column(length = 20)
    private String phone;

    @Column(length = 500)
    private String avatarUrl;

    private Instant emailVerifiedAt;

    private Instant phoneVerifiedAt;

    private Instant lastLoginAt;

    public boolean isProvider() {
        return role == UserRole.PROVIDER || role == UserRole.BOTH;
    }

    public boolean isConsumer() {
        return role == UserRole.CONSUMER || role == UserRole.BOTH;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
        registerEvent(new UserRegisteredEvent(getId(), email, role.name()));
    }

    public void verifyEmail() {
        this.emailVerifiedAt = Instant.now();
    }

    public void verifyPhone() {
        this.phoneVerifiedAt = Instant.now();
    }

    public void updateProfile(String firstName, String lastName, String phone, String avatarUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.avatarUrl = avatarUrl;
    }

    public void recordLogin() {
        this.lastLoginAt = Instant.now();
    }
}
