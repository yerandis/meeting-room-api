package com.yerandis.meetingRoom.mapper;

import com.yerandis.meetingRoom.dto.request.RoomRequest;
import com.yerandis.meetingRoom.dto.response.RoomResponse;
import com.yerandis.meetingRoom.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomRequest request) {
        return new Room(
                request.name().trim(),
                request.capacity()
        );
    }

    public RoomResponse toResponse(Room room) {
        return new RoomResponse(
                room.getId(),
                room.getName(),
                room.getCapacity(),
                room.getCreatedAt()
        );
    }
}
