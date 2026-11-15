package com.yerandis.meetingRoom.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;
import java.util.UUID;

public record ReservationRequest(

        @NotNull(message = "La sala es obligatoria")
        UUID roomId,

        @NotBlank(message = "El responsable es obligatorio")
        @Size(max = 150, message = "El responsable no puede superar los 150 caracteres")
        String responsible,

        @NotNull(message = "La fecha de inicio es obligatoria")
        Instant startTime,

        @NotNull(message = "La fecha de finalización es obligatoria")
        Instant endTime

) {}
