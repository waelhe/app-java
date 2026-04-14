package com.marketplace.notify.application;

import com.marketplace.notify.domain.Notification;
import com.marketplace.notify.domain.NotificationChannel;
import com.marketplace.notify.domain.NotificationType;

import java.time.Instant;
import java.util.UUID;

/**
 * Read-only projection of a Notification entity.
 */
public record NotificationDto(
    UUID id,
    UUID userId,
    NotificationType type,
    NotificationChannel channel,
    String title,
    String content,
    String referenceType,
    UUID referenceId,
    boolean isRead,
    Instant readAt,
    Instant sentAt,
    Instant createdAt
) {
    public static NotificationDto from(Notification notification) {
        return new NotificationDto(
            notification.getId(),
            notification.getUserId(),
            notification.getType(),
            notification.getChannel(),
            notification.getTitle(),
            notification.getContent(),
            notification.getReferenceType(),
            notification.getReferenceId(),
            notification.isRead(),
            notification.getReadAt(),
            notification.getSentAt(),
            notification.getCreatedAt()
        );
    }
}
