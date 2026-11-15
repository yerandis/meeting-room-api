CREATE TABLE reservations (
    id          UUID         PRIMARY KEY,
    room_id     UUID         NOT NULL,
    responsible VARCHAR(150) NOT NULL,
    start_time  TIMESTAMP    NOT NULL,
    end_time    TIMESTAMP    NOT NULL,
    status      VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL,

    CONSTRAINT fk_reservations_room
        FOREIGN KEY (room_id) REFERENCES rooms (id),

    CONSTRAINT chk_reservations_time_range
        CHECK (end_time > start_time),

    CONSTRAINT chk_reservations_status
        CHECK (status IN ('ACTIVE', 'CANCELLED'))
);

CREATE INDEX idx_reservations_room_status_start
    ON reservations (room_id, status, start_time);

CREATE INDEX idx_reservations_start_end
    ON reservations (start_time, end_time);
