package com.marketplace.message.infrastructure;

import com.marketplace.message.domain.Conversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JpaConversationRepository extends JpaRepository<Conversation, UUID> {

    /**
     * Finds a conversation between two participants regardless of order.
     * Matches where (participant1 = p1 AND participant2 = p2) OR (participant1 = p2 AND participant2 = p1).
     */
    @Query("SELECT c FROM Conversation c WHERE " +
           "(c.participant1Id = :p1 AND c.participant2Id = :p2) OR " +
           "(c.participant1Id = :p2 AND c.participant2Id = :p1)")
    Optional<Conversation> findByParticipantIds(@Param("p1") UUID participant1Id, @Param("p2") UUID participant2Id);

    /**
     * Finds all conversations for a participant (as either participant1 or participant2),
     * ordered by lastMessageAt descending.
     */
    @Query("SELECT c FROM Conversation c WHERE " +
           "c.participant1Id = :participantId OR c.participant2Id = :participantId " +
           "ORDER BY c.lastMessageAt DESC")
    List<Conversation> findByParticipantId(@Param("participantId") UUID participantId);
}
