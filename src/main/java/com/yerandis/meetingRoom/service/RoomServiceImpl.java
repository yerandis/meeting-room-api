package com.yerandis.meetingRoom.service;

import com.yerandis.meetingRoom.dto.request.RoomRequest;
import com.yerandis.meetingRoom.dto.response.PageResponse;
import com.yerandis.meetingRoom.dto.response.RoomResponse;
import com.yerandis.meetingRoom.entity.Room;
import com.yerandis.meetingRoom.exception.BusinessException;
import com.yerandis.meetingRoom.exception.ResourceNotFoundException;
import com.yerandis.meetingRoom.mapper.RoomMapper;
import com.yerandis.meetingRoom.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public RoomServiceImpl(RoomRepository roomRepository, RoomMapper roomMapper) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<RoomResponse> findAll(String search, Pageable pageable) {
        Page<Room> rooms = roomRepository.searchRooms(normalizeSearch(search), pageable);
        return new PageResponse<>(rooms.map(roomMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public RoomResponse findById(UUID id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sala", id));
        return roomMapper.toResponse(room);
    }

    @Override
    @Transactional
    public RoomResponse create(RoomRequest request) {
        String name = request.name().trim();
        if (roomRepository.existsByNameIgnoreCase(name)) {
            throw new BusinessException(
                    "ROOM_ALREADY_EXISTS",
                    "Ya existe una sala con el nombre: " + name
            );
        }
        Room saved = roomRepository.save(roomMapper.toEntity(request));
        return roomMapper.toResponse(saved);
    }

    private String normalizeSearch(String search) {
        return (search == null || search.isBlank()) ? null : search.trim();
    }
}
