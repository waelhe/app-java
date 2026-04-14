-- Shared schema for marketplace platform
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Spring Modulith event publication log
CREATE TABLE IF NOT EXISTS event_publication (
    id UUID NOT NULL,
    completion_date TIMESTAMP WITH TIME ZONE,
    event_type VARCHAR(255) NOT NULL,
    listener_id VARCHAR(255) NOT NULL,
    publication_date TIMESTAMP WITH TIME ZONE NOT NULL,
    serialized_event TEXT NOT NULL,
    PRIMARY KEY (id)
);
