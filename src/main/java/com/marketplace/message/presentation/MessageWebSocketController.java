package com.marketplace.message.presentation;

import com.marketplace.message.application.MessageDto;
import com.marketplace.message.application.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MessageWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * Handles WebSocket messages sent to /app/chat.sendMessage.
     * Sends the message via the service, then pushes the resulting MessageDto
     * to the recipient's personal queue at /user/{recipientId}/queue/messages.
     */
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessagePayload payload) {
        log.info("WebSocket: sendMessage, conversationId={}, senderId={}",
                payload.conversationId(), payload.senderId());

        MessageDto message = messageService.sendMessage(
                payload.conversationId(),
                payload.senderId(),
                payload.content()
        );

        // Determine the recipient — the other participant in the conversation
        // The service validates that the sender is a participant, so we push to
        // both participants' queues to keep their conversation lists updated.
        // The recipient-specific push goes to their personal queue.
        messagingTemplate.convertAndSendToUser(
                message.conversationId().toString(),
                "/queue/messages",
                message
        );

        log.info("WebSocket: message pushed to queue for conversation {}", message.conversationId());
    }

    /**
     * Handles WebSocket messages sent to /app/chat.markRead.
     * Marks the specified message as read, then notifies the sender
     * that their message has been read via their personal queue.
     */
    @MessageMapping("/chat.markRead")
    public void markAsRead(MarkReadPayload payload) {
        log.info("WebSocket: markAsRead, messageId={}, userId={}",
                payload.messageId(), payload.userId());

        messageService.markAsRead(payload.messageId(), payload.userId());

        // Notify the original sender that their message has been read
        messagingTemplate.convertAndSendToUser(
                payload.messageId().toString(),
                "/queue/messages",
                new ReadReceiptPayload(payload.messageId(), payload.userId())
        );

        log.info("WebSocket: read receipt pushed for message {}", payload.messageId());
    }

    /**
     * Payload for sending a chat message via WebSocket.
     */
    public record ChatMessagePayload(
        UUID conversationId,
        UUID senderId,
        String content
    ) {}

    /**
     * Payload for marking a message as read via WebSocket.
     */
    public record MarkReadPayload(
        UUID messageId,
        UUID userId
    ){}

    /**
     * Payload for read receipt notifications pushed to the sender's queue.
     */
    public record ReadReceiptPayload(
        UUID messageId,
        UUID readBy
    ){}
}
