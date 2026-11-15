package com.yerandis.meetingRoom.repository;

import com.yerandis.meetingRoom.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    boolean existsByNameIgnoreCase(String name);

    @Query("""
            SELECT r
            FROM Room r
            WHERE :search IS NULL
               OR LOWER(r.name) LIKE LOWER(CONCAT('%', :search, '%'))
            """)
    Page<Room> searchRooms(
            @Param("search") String search,
            Pageable pageable
    );
}
