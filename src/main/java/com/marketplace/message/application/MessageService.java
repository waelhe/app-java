package com.marketplace.message.application;

import com.marketplace.message.domain.Conversation;
import com.marketplace.message.domain.ConversationRepository;
import com.marketplace.message.domain.Message;
import com.marketplace.message.domain.MessageRepository;
import com.marketplace.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;

    /**
     * Starts a new conversation between two participants.
     * If a conversation already exists between these participants, returns the existing one.
     * Optionally associates the conversation with a listing.
     */
    @Transactional
    public ConversationDto startConversation(UUID participant1Id, UUID participant2Id, UUID listingId) {
        // Check if a conversation already exists between these participants
        var existing = conversationRepository.findByParticipantIds(participant1Id, participant2Id);
        if (existing.isPresent()) {
            log.info("Conversation already exists between {} and {}: {}",
                    participant1Id, participant2Id, existing.get().getId());
            return ConversationDto.from(existing.get());
        }

        Instant now = Instant.now();
        Conversation conversation = Conversation.builder()
                .participant1Id(participant1Id)
                .participant2Id(participant2Id)
                .listingId(listingId)
                .lastMessageAt(now)
                .build();

        Conversation saved = conversationRepository.save(conversation);
        log.info("Conversation started: id={}, between {} and {}, listingId={}",
                saved.getId(), participant1Id, participant2Id, listingId);
        return ConversationDto.from(saved);
    }

    /**
     * Sends a message in an existing conversation.
     * Updates the conversation's lastMessagePreview and lastMessageAt.
     * Throws BusinessException if the conversation does not exist.
     */
    @Transactional
    public MessageDto sendMessage(UUID conversationId, UUID senderId, String content) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new BusinessException("Conversation not found"));

        // Validate that the sender is a participant of this conversation
        if (!conversation.getParticipant1Id().equals(senderId) &&
            !conversation.getParticipant2Id().equals(senderId)) {
            throw new BusinessException("Sender is not a participant of this conversation");
        }

        Instant now = Instant.now();

        Message message = Message.builder()
                .conversationId(conversationId)
                .senderId(senderId)
                .content(content)
                .isRead(false)
                .build();

        Message saved = messageRepository.save(message);

        // Update conversation's last message info
        conversation.updateLastMessage(content, now);
        conversationRepository.save(conversation);

        log.info("Message sent: id={}, conversationId={}, senderId={}", saved.getId(), conversationId, senderId);
        return MessageDto.from(saved);
    }

    /**
     * Marks a message as read by the recipient.
     * Validates that the user is the recipient (not the sender) of the message.
     */
    @Transactional
    public void markAsRead(UUID messageId, UUID userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException("Message not found"));

        // Only the recipient (not the sender) can mark a message as read
        if (message.getSenderId().equals(userId)) {
            throw new BusinessException("Cannot mark your own message as unread");
        }

        // Verify the user is a participant of the conversation
        Conversation conversation = conversationRepository.findById(message.getConversationId())
                .orElseThrow(() -> new BusinessException("Conversation not found"));

        if (!conversation.getParticipant1Id().equals(userId) &&
            !conversation.getParticipant2Id().equals(userId)) {
            throw new BusinessException("User is not a participant of this conversation");
        }

        message.markAsRead();
        messageRepository.save(message);
        log.info("Message marked as read: id={}, by user={}", messageId, userId);
    }

    /**
     * Retrieves all conversations for a given user.
     * Returns conversations ordered by lastMessageAt descending (most recent first).
     */
    @Transactional(readOnly = true)
    public List<ConversationDto> getConversations(UUID userId) {
        return conversationRepository.findByParticipantId(userId).stream()
                .map(ConversationDto::from)
                .toList();
    }

    /**
     * Retrieves messages for a given conversation, paginated.
     * Results are ordered by creation time descending (newest first).
     */
    @Transactional(readOnly = true)
    public Page<MessageDto> getMessages(UUID conversationId, Pageable pageable) {
        return messageRepository.findByConversationId(conversationId, pageable)
                .map(MessageDto::from);
    }

    /**
     * Returns the count of unread messages for a given user.
     */
    @Transactional(readOnly = true)
    public long getUnreadCount(UUID userId) {
        return messageRepository.countUnreadByRecipient(userId);
    }
}
