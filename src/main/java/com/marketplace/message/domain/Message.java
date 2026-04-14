package com.marketplace.message.domain;

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
@Table(name = "messages")
public class Message extends BaseEntity {

    @Column(nullable = false)
    private UUID conversationId;

    @Column(nullable = false)
    private UUID senderId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isRead;

    private Instant readAt;

    /**
     * Marks the message as read by the recipient.
     * Sets the read flag and records the read timestamp.
     */
    public void markAsRead() {
        if (!this.isRead) {
            this.isRead = true;
            this.readAt = Instant.now();
        }
    }
}
