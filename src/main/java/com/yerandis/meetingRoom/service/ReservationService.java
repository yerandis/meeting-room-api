package com.yerandis.meetingRoom.service;

import com.yerandis.meetingRoom.dto.request.ReservationRequest;
import com.yerandis.meetingRoom.dto.response.PageResponse;
import com.yerandis.meetingRoom.dto.response.ReservationResponse;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface ReservationService {

    PageResponse<ReservationResponse> findAll(
            String search,
            UUID roomId,
            Instant startTime,
            Instant endTime,
            Pageable pageable
    );

    ReservationResponse findById(UUID id);

    ReservationResponse create(ReservationRequest request);

    ReservationResponse cancel(UUID id);
}
