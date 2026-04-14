package com.marketplace.message.infrastructure;

import com.marketplace.message.domain.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface JpaMessageRepository extends JpaRepository<Message, UUID> {

    /**
     * Finds all messages in a conversation, ordered by creation time descending (newest first).
     */
    Page<Message> findByConversationIdOrderByCreatedAtDesc(UUID conversationId, Pageable pageable);

    /**
     * Finds all unread messages where the user is not the sender
     * and is a participant in the conversation.
     */
    @Query("SELECT m FROM Message m " +
           "JOIN Conversation c ON m.conversationId = c.id " +
           "WHERE m.isRead = false AND m.senderId <> :recipientId " +
           "AND (c.participant1Id = :recipientId OR c.participant2Id = :recipientId)")
    List<Message> findUnreadByRecipient(@Param("recipientId") UUID recipientId);

    /**
     * Counts unread messages for a given recipient.
     */
    @Query("SELECT COUNT(m) FROM Message m " +
           "JOIN Conversation c ON m.conversationId = c.id " +
           "WHERE m.isRead = false AND m.senderId <> :recipientId " +
           "AND (c.participant1Id = :recipientId OR c.participant2Id = :recipientId)")
    long countUnreadByRecipient(@Param("recipientId") UUID recipientId);
}
