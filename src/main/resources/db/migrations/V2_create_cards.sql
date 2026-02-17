CREATE TABLE cards (
    id BIGSERIAL PRIMARY KEY,
    number VARCHAR(19) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL REFERENCES users(id),
    expiration_date DATE NOT NULL,
    balance NUMERIC(19, 2) NOT NULL
)