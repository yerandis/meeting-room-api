package com.yerandis.meetingRoom.controller;

import com.yerandis.meetingRoom.dto.request.RoomRequest;
import com.yerandis.meetingRoom.dto.response.ApiResponse;
import com.yerandis.meetingRoom.dto.response.PageResponse;
import com.yerandis.meetingRoom.dto.response.RoomResponse;
import com.yerandis.meetingRoom.exception.BusinessException;
import com.yerandis.meetingRoom.service.RoomService;
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

import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")
@Validated
public class RoomController {

    private static final Set<String> SORT_FIELDS = Set.of("name", "capacity", "createdAt");

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<RoomResponse>>> findAll(
            @RequestParam(required = false) @Size(max = 100) String search,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Pageable pageable = createPageable(page, size, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(
                "Salas obtenidas exitosamente",
                roomService.findAll(search, pageable)
        ));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Sala obtenida exitosamente",
                roomService.findById(id)
        ));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> create(
            @Valid @RequestBody RoomRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sala creada exitosamente", roomService.create(request)));
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
