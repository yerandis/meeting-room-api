CREATE TABLE rooms (
    id          UUID         PRIMARY KEY,
    name        VARCHAR(120) NOT NULL,
    capacity    INTEGER      NOT NULL,
    created_at  TIMESTAMPTZ  NOT NULL,

    CONSTRAINT uk_rooms_name           UNIQUE (name),
    CONSTRAINT chk_rooms_capacity_positive CHECK (capacity > 0)
);
