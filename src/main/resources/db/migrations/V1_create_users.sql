CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN NOT NULL,
    role VARCHAR(5) NOT NULL
);

ALTER TABLE users ADD CONSTRAINT users_role_check
    CHECK (role IN ('USER', 'ADMIN'));