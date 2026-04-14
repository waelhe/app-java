package com.marketplace.notify.infrastructure;

import com.marketplace.notify.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface JpaNotificationRepository extends JpaRepository<Notification, UUID> {

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    List<Notification> findByUserIdAndIsReadFalse(UUID userId);

    long countByUserIdAndIsReadFalse(UUID userId);
}
