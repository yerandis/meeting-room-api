package com.yerandis.meetingRoom.mapper;

import com.yerandis.meetingRoom.dto.request.ReservationRequest;
import com.yerandis.meetingRoom.dto.response.ReservationResponse;
import com.yerandis.meetingRoom.entity.Reservation;
import com.yerandis.meetingRoom.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class ReservationMapper {

    public Reservation toEntity(ReservationRequest request, Room room) {
        return new Reservation(
                room,
                request.responsible().trim(),
                request.startTime(),
                request.endTime()
        );
    }

    public ReservationResponse toResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getRoom().getId(),
                reservation.getRoom().getName(),
                reservation.getResponsible(),
                reservation.getStartTime(),
                reservation.getEndTime(),
                reservation.getStatus(),
                reservation.getCreatedAt()
        );
    }
}
