package com.marketplace.shared.config;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Slf4j
public class AuditListener {

    @PrePersist
    void onCreate(Object entity) {
        log.debug("Creating entity: {} at {}", entity.getClass().getSimpleName(), Instant.now());
    }

    @PreUpdate
    void onUpdate(Object entity) {
        log.debug("Updating entity: {} at {}", entity.getClass().getSimpleName(), Instant.now());
    }
}
