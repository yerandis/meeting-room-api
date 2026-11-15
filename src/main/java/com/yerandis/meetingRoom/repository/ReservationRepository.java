package com.yerandis.meetingRoom.repository;

import com.yerandis.meetingRoom.entity.Reservation;
import com.yerandis.meetingRoom.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.Instant;
import java.util.UUID;

public interface ReservationRepository
        extends JpaRepository<Reservation, UUID>, JpaSpecificationExecutor<Reservation> {

    boolean existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID roomId,
            ReservationStatus status,
            Instant requestedEnd,
            Instant requestedStart
    );
}