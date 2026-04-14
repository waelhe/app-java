-- REVIEW module tables
CREATE TABLE reviews (
    id UUID NOT NULL,
    booking_id UUID NOT NULL,
    reviewer_id UUID NOT NULL,
    reviewee_id UUID NOT NULL,
    listing_id UUID NOT NULL,
    rating INTEGER NOT NULL CHECK (rating BETWEEN 1 AND 5),
    communication_rating INTEGER CHECK (communication_rating BETWEEN 1 AND 5),
    quality_rating INTEGER CHECK (quality_rating BETWEEN 1 AND 5),
    value_rating INTEGER CHECK (value_rating BETWEEN 1 AND 5),
    comment TEXT,
    provider_response TEXT,
    responded_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_reviews PRIMARY KEY (id),
    CONSTRAINT fk_reviews_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_reviews_reviewer FOREIGN KEY (reviewer_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_reviewee FOREIGN KEY (reviewee_id) REFERENCES users(id),
    CONSTRAINT fk_reviews_listing FOREIGN KEY (listing_id) REFERENCES listings(id),
    CONSTRAINT uq_reviews_booking UNIQUE (booking_id)
);

CREATE INDEX idx_reviews_reviewee ON reviews (reviewee_id);
CREATE INDEX idx_reviews_listing ON reviews (listing_id);
CREATE INDEX idx_reviews_rating ON reviews (rating);
