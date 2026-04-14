package com.marketplace.message.application;

import com.marketplace.message.domain.Message;

import java.time.Instant;
import java.util.UUID;

public record MessageDto(
    UUID id,
    UUID conversationId,
    UUID senderId,
    String content,
    boolean isRead,
    Instant readAt,
    Instant createdAt
) {
    public static MessageDto from(Message message) {
        return new MessageDto(
            message.getId(),
            message.getConversationId(),
            message.getSenderId(),
            message.getContent(),
            message.isRead(),
            message.getReadAt(),
            message.getCreatedAt()
        );
    }
}
