package com.marketplace.notify.application;

import com.marketplace.notify.domain.Notification;
import com.marketplace.notify.domain.NotificationChannel;
import com.marketplace.notify.domain.NotificationPreference;
import com.marketplace.notify.domain.NotificationRepository;
import com.marketplace.notify.domain.NotificationType;
import com.marketplace.notify.infrastructure.JpaNotificationPreferenceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final JpaNotificationPreferenceRepository preferenceRepository;

    /**
     * Sends a notification to a user via the specified channel.
     * Persists the notification and marks sentAt. Runs asynchronously.
     */
    @Async
    @Transactional
    public void sendNotification(UUID userId, NotificationType type, NotificationChannel channel,
                                  String title, String content,
                                  String referenceType, UUID referenceId) {
        // Check user preference before sending
        if (!isNotificationEnabled(userId, type, channel)) {
            log.debug("Notification suppressed for user {} type {} channel {} (disabled preference)",
                    userId, type, channel);
            return;
        }

        Notification notification = Notification.builder()
                .userId(userId)
                .type(type)
                .channel(channel)
                .title(title)
                .content(content)
                .referenceType(referenceType)
                .referenceId(referenceId)
                .isRead(false)
                .sentAt(Instant.now())
                .build();

        notificationRepository.save(notification);
        log.info("Notification sent: userId={}, type={}, channel={}, title={}", userId, type, channel, title);

        // TODO: Integrate with actual email/SMS/push delivery gateways based on channel
        // IN_APP notifications are already persisted and available via API
        // EMAIL → send via email gateway
        // SMS → send via SMS gateway (e.g., Twilio)
        // PUSH → send via push notification service (e.g., Firebase Cloud Messaging)
    }

    /**
     * Marks a specific notification as read. Validates that the notification
     * belongs to the given user.
     */
    @Transactional
    public void markAsRead(UUID notificationId, UUID userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found: " + notificationId));

        if (!notification.getUserId().equals(userId)) {
            throw new IllegalArgumentException("Notification does not belong to user: " + userId);
        }

        notification.markAsRead();
        notificationRepository.save(notification);
    }

    /**
     * Marks all unread notifications for a user as read.
     */
    @Transactional
    public void markAllAsRead(UUID userId) {
        List<Notification> unread = notificationRepository.findUnreadByUserId(userId);
        for (Notification notification : unread) {
            notification.markAsRead();
            notificationRepository.save(notification);
        }
        log.info("Marked {} notifications as read for user {}", unread.size(), userId);
    }

    /**
     * Retrieves paginated notifications for a user.
     */
    @Transactional(readOnly = true)
    public Page<NotificationDto> getNotifications(UUID userId, Pageable pageable) {
        return notificationRepository.findByUserId(userId, pageable)
                .map(NotificationDto::from);
    }

    /**
     * Returns the count of unread notifications for a user.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    /**
     * Updates a notification preference for a user. Creates the preference
     * if it does not exist.
     */
    @Transactional
    public void updatePreference(UUID userId, NotificationType type, NotificationChannel channel, boolean enabled) {
        NotificationPreference preference = preferenceRepository
                .findByUserIdAndNotificationTypeAndChannel(userId, type, channel)
                .orElseGet(() -> NotificationPreference.builder()
                        .userId(userId)
                        .notificationType(type)
                        .channel(channel)
                        .enabled(enabled)
                        .build());

        preference.setEnabled(enabled);
        preferenceRepository.save(preference);
        log.info("Notification preference updated: userId={}, type={}, channel={}, enabled={}",
                userId, type, channel, enabled);
    }

    /**
     * Retrieves all notification preferences for a user.
     */
    @Transactional(readOnly = true)
    public List<NotificationPreference> getPreferences(UUID userId) {
        return preferenceRepository.findByUserId(userId);
    }

    /**
     * Checks if a notification type/channel is enabled for a user.
     * Returns true by default if no preference is set.
     */
    private boolean isNotificationEnabled(UUID userId, NotificationType type, NotificationChannel channel) {
        return preferenceRepository
                .findByUserIdAndNotificationTypeAndChannel(userId, type, channel)
                .map(NotificationPreference::isEnabled)
                .orElse(true); // Default: enabled if no preference exists
    }
}
