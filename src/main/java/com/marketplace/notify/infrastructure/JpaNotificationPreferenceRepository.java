package com.marketplace.notify.infrastructure;

import com.marketplace.notify.domain.NotificationChannel;
import com.marketplace.notify.domain.NotificationPreference;
import com.marketplace.notify.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaNotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    Optional<NotificationPreference> findByUserIdAndNotificationTypeAndChannel(
            UUID userId, NotificationType notificationType, NotificationChannel channel);

    List<NotificationPreference> findByUserId(UUID userId);
}
