package com.marketplace.message.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConversationRepository {

    Conversation save(Conversation conversation);

    Optional<Conversation> findById(UUID id);

    /**
     * Finds a conversation between two specific participants.
     * Participant order is irrelevant — matches regardless of which user is participant1 or participant2.
     */
    Optional<Conversation> findByParticipantIds(UUID participant1Id, UUID participant2Id);

    /**
     * Finds all conversations for a given participant (either as participant1 or participant2).
     * Results are ordered by lastMessageAt descending (most recent first).
     */
    List<Conversation> findByParticipantId(UUID participantId);
}
