package com.marketplace.message.domain;

import com.marketplace.shared.domain.AggregateRoot;
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
@Table(name = "conversations")
public class Conversation extends AggregateRoot {

    @Column(nullable = false)
    private UUID participant1Id;

    @Column(nullable = false)
    private UUID participant2Id;

    @Column
    private UUID listingId;

    @Column(nullable = false)
    private Instant lastMessageAt;

    @Column(length = 200)
    private String lastMessagePreview;

    /**
     * Updates the conversation's last message preview and timestamp.
     * Called whenever a new message is sent in this conversation.
     */
    public void updateLastMessage(String preview, Instant timestamp) {
        if (preview != null && preview.length() > 200) {
            this.lastMessagePreview = preview.substring(0, 197) + "...";
        } else {
            this.lastMessagePreview = preview;
        }
        this.lastMessageAt = timestamp;
    }
}
