package com.yerandis.meetingRoom.service;

import com.yerandis.meetingRoom.dto.request.ReservationRequest;
import com.yerandis.meetingRoom.dto.response.PageResponse;
import com.yerandis.meetingRoom.dto.response.ReservationResponse;
import com.yerandis.meetingRoom.entity.Reservation;
import com.yerandis.meetingRoom.entity.ReservationStatus;
import com.yerandis.meetingRoom.entity.Room;
import com.yerandis.meetingRoom.exception.BusinessException;
import com.yerandis.meetingRoom.exception.ResourceNotFoundException;
import com.yerandis.meetingRoom.mapper.ReservationMapper;
import com.yerandis.meetingRoom.repository.ReservationRepository;
import com.yerandis.meetingRoom.repository.RoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final ReservationMapper reservationMapper;
    private final RoomRepository roomRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            ReservationMapper reservationMapper,
            RoomRepository roomRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.reservationMapper = reservationMapper;
        this.roomRepository = roomRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReservationResponse> findAll(
            String search, UUID roomId,
            Instant startTime, Instant endTime,
            Pageable pageable
    ) {
        if (startTime != null && endTime != null && !endTime.isAfter(startTime)) {
            throw new BusinessException(
                    "INVALID_TIME_RANGE",
                    "La fecha de finalización debe ser posterior a la de inicio"
            );
        }
        Page<Reservation> reservations = reservationRepository.searchReservations(
                normalizeSearch(search), roomId, startTime, endTime, pageable
        );
        return new PageResponse<>(reservations.map(reservationMapper::toResponse));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse findById(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        return reservationMapper.toResponse(reservation);
    }

    @Override
    @Transactional
    public ReservationResponse create(ReservationRequest request) {
        if (!request.endTime().isAfter(request.startTime())) {
            throw new BusinessException(
                    "INVALID_TIME_RANGE",
                    "La fecha de finalización debe ser posterior a la de inicio"
            );
        }
        Room room = roomRepository.findById(request.roomId())
                .orElseThrow(() -> new ResourceNotFoundException("Sala", request.roomId()));

        boolean overlapping = reservationRepository
                .existsByRoomIdAndStatusAndStartTimeLessThanAndEndTimeGreaterThan(
                        room.getId(),
                        ReservationStatus.ACTIVE,
                        request.endTime(),
                        request.startTime()
                );
        if (overlapping) {
            throw new BusinessException(
                    "RESERVATION_OVERLAP",
                    "La sala ya tiene una reserva activa en ese intervalo"
            );
        }
        Reservation saved = reservationRepository.save(
                reservationMapper.toEntity(request, room)
        );
        return reservationMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public ReservationResponse cancel(UUID id) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", id));
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            throw new BusinessException(
                    "RESERVATION_ALREADY_CANCELLED",
                    "La reserva ya se encuentra cancelada"
            );
        }
        reservation.setStatus(ReservationStatus.CANCELLED);
        return reservationMapper.toResponse(reservation);
    }

    private String normalizeSearch(String search) {
        return (search == null || search.isBlank()) ? null : search.trim();
    }
}
