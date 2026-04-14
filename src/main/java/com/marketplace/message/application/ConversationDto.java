package com.marketplace.message.application;

import com.marketplace.message.domain.Conversation;

import java.time.Instant;
import java.util.UUID;

public record ConversationDto(
    UUID id,
    UUID participant1Id,
    UUID participant2Id,
    UUID listingId,
    Instant lastMessageAt,
    String lastMessagePreview,
    Instant createdAt
) {
    public static ConversationDto from(Conversation conversation) {
        return new ConversationDto(
            conversation.getId(),
            conversation.getParticipant1Id(),
            conversation.getParticipant2Id(),
            conversation.getListingId(),
            conversation.getLastMessageAt(),
            conversation.getLastMessagePreview(),
            conversation.getCreatedAt()
        );
    }
}
