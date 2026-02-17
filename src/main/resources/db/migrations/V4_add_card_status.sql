ALTER TABLE cards
ADD COLUMN status VARCHAR(10);

ALTER TABLE cards ADD CONSTRAINT card_status_check
    CHECK (status IN ('ACTIVE', 'BLOCKED', 'EXPIRED'));