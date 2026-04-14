package com.marketplace.message.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository {

    Message save(Message message);

    Optional<Message> findById(UUID id);

    /**
     * Finds all messages in a conversation, paginated and ordered by creation time.
     */
    Page<Message> findByConversationId(UUID conversationId, Pageable pageable);

    /**
     * Finds all unread messages where the recipient is not the sender
     * (i.e., messages in conversations where the user is a participant but not the sender).
     */
    List<Message> findUnreadByRecipient(UUID recipientId);

    /**
     * Counts unread messages for a given recipient.
     */
    long countUnreadByRecipient(UUID recipientId);
}
