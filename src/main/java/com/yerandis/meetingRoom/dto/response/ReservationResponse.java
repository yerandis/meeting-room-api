package com.yerandis.meetingRoom.dto.response;

import com.yerandis.meetingRoom.entity.ReservationStatus;

import java.time.Instant;
import java.util.UUID;

public record ReservationResponse(
        UUID id,
        UUID roomId,
        String roomName,
        String responsible,
        Instant startTime,
        Instant endTime,
        ReservationStatus status,
        Instant createdAt
) {}
