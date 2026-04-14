package com.marketplace.notify.presentation;

import com.marketplace.notify.application.NotificationDto;
import com.marketplace.notify.application.NotificationService;
import com.marketplace.notify.domain.NotificationChannel;
import com.marketplace.notify.domain.NotificationPreference;
import com.marketplace.notify.domain.NotificationType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "Notification and preference management")
@NamedInterface("api")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get paginated notifications for the current user")
    public ResponseEntity<Page<NotificationDto>> getNotifications(
            @AuthenticationPrincipal String userId,
            Pageable pageable
    ) {
        Page<NotificationDto> notifications = notificationService.getNotifications(
                UUID.fromString(userId), pageable);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread notification count for the current user")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
            @AuthenticationPrincipal String userId
    ) {
        long count = notificationService.getUnreadCount(UUID.fromString(userId));
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a specific notification as read")
    public ResponseEntity<Void> markAsRead(
            @AuthenticationPrincipal String userId,
            @PathVariable UUID id
    ) {
        notificationService.markAsRead(id, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read for the current user")
    public ResponseEntity<Void> markAllAsRead(
            @AuthenticationPrincipal String userId
    ) {
        notificationService.markAllAsRead(UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/preferences")
    @Operation(summary = "Get notification preferences for the current user")
    public ResponseEntity<List<NotificationPreference>> getPreferences(
            @AuthenticationPrincipal String userId
    ) {
        List<NotificationPreference> preferences = notificationService.getPreferences(UUID.fromString(userId));
        return ResponseEntity.ok(preferences);
    }

    @PutMapping("/preferences")
    @Operation(summary = "Update a notification preference for the current user")
    public ResponseEntity<Void> updatePreference(
            @AuthenticationPrincipal String userId,
            @RequestBody UpdatePreferenceRequest request
    ) {
        notificationService.updatePreference(
                UUID.fromString(userId),
                request.notificationType(),
                request.channel(),
                request.enabled()
        );
        return ResponseEntity.noContent().build();
    }

    /**
     * Inner record for unread count response.
     */
    public record UnreadCountResponse(long unreadCount) {}

    /**
     * Inner record for preference update request.
     */
    public record UpdatePreferenceRequest(
            NotificationType notificationType,
            NotificationChannel channel,
            boolean enabled
    ) {}
}
