-- MESSAGE module tables
CREATE TABLE conversations (
    id UUID NOT NULL,
    participant1_id UUID NOT NULL,
    participant2_id UUID NOT NULL,
    listing_id UUID,
    last_message_at TIMESTAMP WITH TIME ZONE,
    last_message_preview VARCHAR(200),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_conversations PRIMARY KEY (id),
    CONSTRAINT fk_conversations_participant1 FOREIGN KEY (participant1_id) REFERENCES users(id),
    CONSTRAINT fk_conversations_participant2 FOREIGN KEY (participant2_id) REFERENCES users(id),
    CONSTRAINT chk_conversations_participants CHECK (participant1_id != participant2_id)
);

CREATE INDEX idx_conversations_participant1 ON conversations (participant1_id);
CREATE INDEX idx_conversations_participant2 ON conversations (participant2_id);

CREATE TABLE messages (
    id UUID NOT NULL,
    conversation_id UUID NOT NULL,
    sender_id UUID NOT NULL,
    content TEXT NOT NULL,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_messages PRIMARY KEY (id),
    CONSTRAINT fk_messages_conversation FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE,
    CONSTRAINT fk_messages_sender FOREIGN KEY (sender_id) REFERENCES users(id)
);

CREATE INDEX idx_messages_conversation ON messages (conversation_id, created_at);
CREATE INDEX idx_messages_unread ON messages (is_read, conversation_id) WHERE is_read = FALSE;
