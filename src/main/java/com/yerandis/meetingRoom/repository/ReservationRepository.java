package com.yerandis.meetingRoom.repository;

import com.yerandis.meetingRoom.entity.Reservation;
import com.yerandis.meetingRoom.entity.ReservationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    boolean existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
            UUID roomId,
            ReservationStatus status,
            Instant requestedEnd,
            Instant requestedStart
    );

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN r.room room
            WHERE (:search IS NULL
                OR LOWER(r.responsible) LIKE LOWER(CONCAT('%', :search, '%'))
                OR LOWER(room.name) LIKE LOWER(CONCAT('%', :search, '%')))
              AND (:roomId IS NULL OR room.id = :roomId)
              AND (:startTime IS NULL OR r.endTime > :startTime)
              AND (:endTime IS NULL OR r.startTime < :endTime)
            """)
    Page<Reservation> searchReservations(
            @Param("search") String search,
            @Param("roomId") UUID roomId,
            @Param("startTime") Instant startTime,
            @Param("endTime") Instant endTime,
            Pageable pageable
    );
}
