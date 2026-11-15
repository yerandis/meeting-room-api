package com.yerandis.meetingRoom.controller;

import com.yerandis.meetingRoom.dto.request.ReservationRequest;
import com.yerandis.meetingRoom.dto.response.ApiResponse;
import com.yerandis.meetingRoom.dto.response.PageResponse;
import com.yerandis.meetingRoom.dto.response.ReservationResponse;
import com.yerandis.meetingRoom.exception.BusinessException;
import com.yerandis.meetingRoom.service.ReservationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/reservations")
@Validated
public class ReservationController {

    private static final Set<String> SORT_FIELDS =
            Set.of("startTime", "endTime", "createdAt", "responsible");

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReservationResponse>>> findAll(
            @RequestParam(required = false) @Size(max = 100) String search,
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) Instant startTime,
            @RequestParam(required = false) Instant endTime,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "startTime") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(
                "Reservas obtenidas exitosamente",
                reservationService.findAll(search, roomId, startTime, endTime, pageable)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReservationResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Reserva obtenida exitosamente",
                reservationService.findById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ReservationResponse>> create(
            @Valid @RequestBody ReservationRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Reserva creada exitosamente", reservationService.create(request)));
    }

    @PatchMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<ReservationResponse>> cancel(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Reserva cancelada exitosamente",
                reservationService.cancel(id)
        ));
    }

    private Pageable createPageable(int page, int size, String sortBy, String sortDir) {
        if (!SORT_FIELDS.contains(sortBy)) {
            throw new BusinessException("INVALID_SORT_FIELD", "Campo de ordenamiento no permitido");
        }
        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDir);
        } catch (IllegalArgumentException e) {
            throw new BusinessException("INVALID_SORT_DIRECTION", "La dirección debe ser asc o desc");
        }
        return PageRequest.of(page, size, Sort.by(direction, sortBy));
    }
}
