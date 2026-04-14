package com.marketplace.message.application;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SendMessageRequest(
    @NotNull(message = "Conversation ID is required")
    UUID conversationId,

    @NotBlank(message = "Message content is required")
    String content
) {}
