package com.marketplace.notify.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationRepository {

    Notification save(Notification notification);

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    List<Notification> findUnreadByUserId(UUID userId);

    long countUnreadByUserId(UUID userId);

    Optional<Notification> findById(UUID id);
}
