package com.marketplace.notify.domain;

import com.marketplace.shared.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "notification_preferences",
       uniqueConstraints = @UniqueConstraint(columnNames = {"userId", "notificationType", "channel"}))
public class NotificationPreference extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50, name = "notificationType")
    private NotificationType notificationType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private NotificationChannel channel;

    @Column(nullable = false)
    private boolean enabled;

    /**
     * Updates the enabled status of this notification preference.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
