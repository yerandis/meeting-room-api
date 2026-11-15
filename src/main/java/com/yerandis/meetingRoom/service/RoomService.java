package com.yerandis.meetingRoom.service;

import com.yerandis.meetingRoom.dto.request.RoomRequest;
import com.yerandis.meetingRoom.dto.response.PageResponse;
import com.yerandis.meetingRoom.dto.response.RoomResponse;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface RoomService {

    PageResponse<RoomResponse> findAll(String search, Pageable pageable);

    RoomResponse findById(UUID id);

    RoomResponse create(RoomRequest request);
}
