-- PAYMENT module tables
CREATE TABLE payments (
    id UUID NOT NULL,
    booking_id UUID NOT NULL,
    consumer_id UUID NOT NULL,
    provider_id UUID NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    platform_fee DECIMAL(19,2),
    platform_fee_currency VARCHAR(3),
    provider_payout DECIMAL(19,2),
    provider_payout_currency VARCHAR(3),
    status VARCHAR(50) NOT NULL CHECK (status IN ('PENDING_HOLD', 'HELD', 'CAPTURED', 'REFUNDED', 'FROZEN')),
    stripe_payment_intent_id VARCHAR(255),
    stripe_transfer_id VARCHAR(255),
    hold_at TIMESTAMP WITH TIME ZONE,
    captured_at TIMESTAMP WITH TIME ZONE,
    refunded_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT pk_payments PRIMARY KEY (id),
    CONSTRAINT fk_payments_booking FOREIGN KEY (booking_id) REFERENCES bookings(id),
    CONSTRAINT fk_payments_consumer FOREIGN KEY (consumer_id) REFERENCES users(id),
    CONSTRAINT fk_payments_provider FOREIGN KEY (provider_id) REFERENCES users(id),
    CONSTRAINT uq_payments_booking UNIQUE (booking_id)
);

CREATE INDEX idx_payments_provider ON payments (provider_id);
CREATE INDEX idx_payments_status ON payments (status);
