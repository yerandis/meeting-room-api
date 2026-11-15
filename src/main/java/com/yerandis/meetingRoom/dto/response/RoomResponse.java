package com.yerandis.meetingRoom.dto.response;

import java.time.Instant;
import java.util.UUID;

public record RoomResponse(
        UUID id,
        String name,
        Integer capacity,
        Instant createdAt
) {}
