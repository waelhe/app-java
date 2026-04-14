-- NOTIFY module tables
CREATE TABLE notifications (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL CHECK (channel IN ('IN_APP', 'EMAIL', 'SMS', 'PUSH')),
    title VARCHAR(200) NOT NULL,
    content TEXT,
    reference_type VARCHAR(50),
    reference_id UUID,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMP WITH TIME ZONE,
    sent_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_notifications PRIMARY KEY (id),
    CONSTRAINT fk_notifications_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_notifications_user ON notifications (user_id, created_at DESC);
CREATE INDEX idx_notifications_unread ON notifications (user_id, is_read) WHERE is_read = FALSE;

CREATE TABLE notification_preferences (
    id UUID NOT NULL,
    user_id UUID NOT NULL,
    notification_type VARCHAR(50) NOT NULL,
    channel VARCHAR(20) NOT NULL CHECK (channel IN ('IN_APP', 'EMAIL', 'SMS', 'PUSH')),
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_notification_preferences PRIMARY KEY (id),
    CONSTRAINT uq_notification_pref UNIQUE (user_id, notification_type, channel),
    CONSTRAINT fk_notification_pref_user FOREIGN KEY (user_id) REFERENCES users(id)
);
