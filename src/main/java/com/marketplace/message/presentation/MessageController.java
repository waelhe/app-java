package com.marketplace.message.presentation;

import com.marketplace.message.application.ConversationDto;
import com.marketplace.message.application.MessageDto;
import com.marketplace.message.application.MessageService;
import com.marketplace.message.application.SendMessageRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.modulith.NamedInterface;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
@Tag(name = "Messages", description = "Messaging and conversation management")
@NamedInterface("api")
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/conversations")
    @Operation(summary = "Start a new conversation")
    public ResponseEntity<ConversationDto> startConversation(
        @AuthenticationPrincipal String userId,
        @RequestParam UUID participant2Id,
        @RequestParam(required = false) UUID listingId
    ) {
        ConversationDto conversation = messageService.startConversation(
                UUID.fromString(userId), participant2Id, listingId);
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }

    @PostMapping("/send")
    @Operation(summary = "Send a message in a conversation")
    public ResponseEntity<MessageDto> sendMessage(
        @AuthenticationPrincipal String userId,
        @Valid @RequestBody SendMessageRequest request
    ) {
        MessageDto message = messageService.sendMessage(
                request.conversationId(), UUID.fromString(userId), request.content());
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @GetMapping("/conversations")
    @Operation(summary = "Get all conversations for the current user")
    public ResponseEntity<List<ConversationDto>> getConversations(
        @AuthenticationPrincipal String userId
    ) {
        return ResponseEntity.ok(messageService.getConversations(UUID.fromString(userId)));
    }

    @GetMapping("/conversations/{conversationId}/messages")
    @Operation(summary = "Get messages in a conversation")
    public ResponseEntity<Page<MessageDto>> getMessages(
        @PathVariable UUID conversationId,
        Pageable pageable
    ) {
        return ResponseEntity.ok(messageService.getMessages(conversationId, pageable));
    }

    @GetMapping("/unread-count")
    @Operation(summary = "Get unread message count for the current user")
    public ResponseEntity<UnreadCountResponse> getUnreadCount(
        @AuthenticationPrincipal String userId
    ) {
        long count = messageService.getUnreadCount(UUID.fromString(userId));
        return ResponseEntity.ok(new UnreadCountResponse(count));
    }

    @PutMapping("/{messageId}/read")
    @Operation(summary = "Mark a message as read")
    public ResponseEntity<Void> markAsRead(
        @AuthenticationPrincipal String userId,
        @PathVariable UUID messageId
    ) {
        messageService.markAsRead(messageId, UUID.fromString(userId));
        return ResponseEntity.noContent().build();
    }

    /**
     * Inner record for unread count response.
     */
    public record UnreadCountResponse(long unreadCount) {}
}
