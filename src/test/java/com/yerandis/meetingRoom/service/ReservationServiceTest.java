package com.yerandis.meetingRoom.service;

import com.yerandis.meetingRoom.dto.request.ReservationRequest;
import com.yerandis.meetingRoom.entity.Reservation;
import com.yerandis.meetingRoom.entity.ReservationStatus;
import com.yerandis.meetingRoom.entity.Room;
import com.yerandis.meetingRoom.exception.BusinessException;
import com.yerandis.meetingRoom.exception.ResourceNotFoundException;
import com.yerandis.meetingRoom.mapper.ReservationMapper;
import com.yerandis.meetingRoom.repository.ReservationRepository;
import com.yerandis.meetingRoom.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    private static final Instant START  = Instant.parse("2026-11-16T14:00:00Z");
    private static final Instant END    = Instant.parse("2026-11-16T15:00:00Z");

    @Mock RoomRepository roomRepository;
    @Mock ReservationRepository reservationRepository;
    @Mock ReservationMapper reservationMapper;

    @InjectMocks ReservationServiceImpl reservationService;

    @Test
    void create_whenEndTimeBeforeStartTime_throwsBusinessException() {
        ReservationRequest req = new ReservationRequest(UUID.randomUUID(), "Ulises", END, START);

        assertThatThrownBy(() -> reservationService.create(req))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.getCode()).isEqualTo("INVALID_TIME_RANGE"));
    }

    @Test
    void create_whenRoomNotFound_throwsResourceNotFoundException() {
        UUID roomId = UUID.randomUUID();
        ReservationRequest req = new ReservationRequest(roomId, "Ulises", START, END);
        given(roomRepository.findById(roomId)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.create(req))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void create_whenOverlap_throwsBusinessException() {
        UUID roomId = UUID.randomUUID();
        ReservationRequest req = new ReservationRequest(roomId, "Ulises", START, END);
        given(roomRepository.findById(roomId)).willReturn(Optional.of(new Room("Sala A", 10)));
        given(reservationRepository
                .existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(any(), any(), any(), any()))
                .willReturn(true);

        assertThatThrownBy(() -> reservationService.create(req))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.getCode()).isEqualTo("RESERVATION_OVERLAP"));
    }

    @Test
    void cancel_whenAlreadyCancelled_throwsBusinessException() {
        UUID id = UUID.randomUUID();
        Reservation reservation = new Reservation(new Room("Sala A", 10), "Ulises", START, END);
        reservation.setStatus(ReservationStatus.CANCELLED);
        given(reservationRepository.findById(id)).willReturn(Optional.of(reservation));

        assertThatThrownBy(() -> reservationService.cancel(id))
                .isInstanceOfSatisfying(BusinessException.class,
                        e -> assertThat(e.getCode()).isEqualTo("RESERVATION_ALREADY_CANCELLED"));
    }
}