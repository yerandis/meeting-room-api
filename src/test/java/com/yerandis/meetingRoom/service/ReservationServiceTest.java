package com.yerandis.meetingRoom.service;

import com.yerandis.meetingRoom.dto.request.ReservationRequest;
import com.yerandis.meetingRoom.exception.BusinessException;
import com.yerandis.meetingRoom.exception.ResourceNotFoundException;
import com.yerandis.meetingRoom.entity.ReservationStatus;
import com.yerandis.meetingRoom.entity.Room;
import com.yerandis.meetingRoom.entity.Reservation;
import com.yerandis.meetingRoom.mapper.ReservationMapper;
import com.yerandis.meetingRoom.repository.ReservationRepository;
import com.yerandis.meetingRoom.repository.RoomRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

// TEST UNITARIO — sin Spring, sin BD.
// Quita @Disabled de cada test cuando lo vayas a implementar.

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock RoomRepository roomRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock ReservationMapper reservationMapper;

    @InjectMocks ReservationServiceImpl reservationService;

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void create_whenEndTimeBeforeStartTime_throwsBusinessException() {
        // Arrange: ReservationRequest con endTime < startTime
        // Act + Assert: lanza BusinessException con code INVALID_TIME_RANGE
    }

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void create_whenRoomNotFound_throwsResourceNotFoundException() {
        // Arrange: roomRepository.findById devuelve Optional.empty()
        // Act + Assert: lanza ResourceNotFoundException
    }

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void create_whenOverlap_throwsBusinessException() {
        // Arrange: roomRepository devuelve sala, existsBy... devuelve true
        // Act + Assert: lanza BusinessException con code RESERVATION_OVERLAP
    }

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void cancel_whenAlreadyCancelled_throwsBusinessException() {
        // Arrange: reservationRepository devuelve reserva con status CANCELLED
        // Act + Assert: lanza BusinessException con code RESERVATION_ALREADY_CANCELLED
    }
}
