package com.yerandis.meetingRoom.repository;

import com.yerandis.meetingRoom.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID>, JpaSpecificationExecutor<Room> {

    boolean existsByNameIgnoreCase(String name);
}