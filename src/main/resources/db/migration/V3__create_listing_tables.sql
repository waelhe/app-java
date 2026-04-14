-- LISTING module tables
CREATE TABLE listings (
    id UUID NOT NULL,
    provider_id UUID NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    subcategory VARCHAR(100),
    amount DECIMAL(19,2),
    currency VARCHAR(3),
    pricing_type VARCHAR(50) CHECK (pricing_type IN ('HOURLY', 'DAILY', 'FIXED', NULL)),
    status VARCHAR(50) NOT NULL CHECK (status IN ('DRAFT', 'ACTIVE', 'INACTIVE', 'PAUSED', 'ARCHIVED')),
    location VARCHAR(100),
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    amenities TEXT,
    rules TEXT,
    activated_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_listings PRIMARY KEY (id),
    CONSTRAINT fk_listings_provider FOREIGN KEY (provider_id) REFERENCES users(id)
);

CREATE INDEX idx_listings_provider ON listings (provider_id);
CREATE INDEX idx_listings_category ON listings (category);
CREATE INDEX idx_listings_status ON listings (status);
CREATE INDEX idx_listings_location ON listings (location);
CREATE INDEX idx_listings_active ON listings (status, category) WHERE status = 'ACTIVE';

CREATE TABLE listing_media (
    id UUID NOT NULL,
    listing_id UUID NOT NULL,
    url VARCHAR(500) NOT NULL,
    media_type VARCHAR(50) CHECK (media_type IN ('IMAGE', 'VIDEO', NULL)),
    display_order INTEGER,
    alt_text VARCHAR(500),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_listing_media PRIMARY KEY (id),
    CONSTRAINT fk_listing_media_listing FOREIGN KEY (listing_id) REFERENCES listings(id) ON DELETE CASCADE
);

CREATE INDEX idx_listing_media_listing ON listing_media (listing_id);
