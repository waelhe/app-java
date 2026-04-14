package com.marketplace.notify.domain;

import com.marketplace.shared.domain.BaseEntity;
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
@Table(name = "notifications")
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private NotificationType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 100)
    private String referenceType;

    private UUID referenceId;

    @Column(nullable = false)
    private boolean isRead;

    private Instant readAt;

    private Instant sentAt;

    /**
     * Marks the notification as read.
     * Sets the read flag and records the read timestamp.
     * Idempotent — no-op if already read.
     */
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = Instant.now();
        }
    }
}
