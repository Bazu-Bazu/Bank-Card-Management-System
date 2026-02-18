CREATE TABLE IF NOT EXISTS transfers (
    id BIGSERIAL PRIMARY KEY,
    from_card_id BIGINT NOT NULL REFERENCES cards(id),
    to_card_id BIGINT NOT NULL REFERENCES cards(id),
    amount NUMERIC(19, 2) NOT NULL,
    created_at TIMESTAMP(6) NOT NULL
);
