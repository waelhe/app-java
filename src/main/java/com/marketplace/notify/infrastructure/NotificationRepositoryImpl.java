package com.marketplace.notify.infrastructure;

import com.marketplace.notify.domain.Notification;
import com.marketplace.notify.domain.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaRepository;

    @Override
    public Notification save(Notification notification) {
        return jpaRepository.save(notification);
    }

    @Override
    public Page<Notification> findByUserId(UUID userId, Pageable pageable) {
        return jpaRepository.findByUserId(userId, pageable);
    }

    @Override
    public List<Notification> findUnreadByUserId(UUID userId) {
        return jpaRepository.findByUserIdAndIsReadFalse(userId);
    }

    @Override
    public long countUnreadByUserId(UUID userId) {
        return jpaRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Override
    public Optional<Notification> findById(UUID id) {
        return jpaRepository.findById(id);
    }
}
