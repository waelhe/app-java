-- BOOKING module tables
CREATE TABLE bookings (
    id UUID NOT NULL,
    consumer_id UUID NOT NULL,
    provider_id UUID NOT NULL,
    listing_id UUID NOT NULL,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    total_amount DECIMAL(19,2),
    total_currency VARCHAR(3),
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED', 'DISPUTED')),
    consumer_notes VARCHAR(1000),
    cancellation_reason VARCHAR(1000),
    confirmed_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_bookings_consumer FOREIGN KEY (consumer_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_provider FOREIGN KEY (provider_id) REFERENCES users(id),
    CONSTRAINT fk_bookings_listing FOREIGN KEY (listing_id) REFERENCES listings(id),
    CONSTRAINT chk_bookings_dates CHECK (end_date > start_date)
);

CREATE INDEX idx_bookings_consumer ON bookings (consumer_id);
CREATE INDEX idx_bookings_provider ON bookings (provider_id);
CREATE INDEX idx_bookings_listing ON bookings (listing_id);
CREATE INDEX idx_bookings_status ON bookings (status);
CREATE INDEX idx_bookings_dates ON bookings (start_date, end_date);
