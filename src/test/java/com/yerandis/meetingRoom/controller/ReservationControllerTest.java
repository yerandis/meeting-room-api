package com.yerandis.meetingRoom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yerandis.meetingRoom.dto.request.ReservationRequest;
import com.yerandis.meetingRoom.dto.request.RoomRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.Instant;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void createReservation_whenOverlap_returns409() throws Exception {
        UUID roomId = createRoom();
        postReservation(roomId, "2026-11-16T14:00:00Z", "2026-11-16T15:00:00Z", true)
                .andExpect(status().isCreated());

        postReservation(roomId, "2026-11-16T14:30:00Z", "2026-11-16T15:30:00Z", true)
                .andExpect(status().isConflict());
    }

    @Test
    void cancelReservation_authenticated_returns200() throws Exception {
        UUID roomId = createRoom();
        String body = postReservation(roomId, "2026-11-17T10:00:00Z", "2026-11-17T11:00:00Z", true)
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        String reservationId = objectMapper.readTree(body).path("data").path("id").asText();

        mockMvc.perform(patch("/api/reservations/{id}/cancel", reservationId)
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELLED"));
    }

    @Test
    void createReservation_withoutAuth_returns401() throws Exception {
        postReservation(UUID.randomUUID(), "2026-11-18T10:00:00Z", "2026-11-18T11:00:00Z", false)
                .andExpect(status().isUnauthorized());
    }

    private UUID createRoom() throws Exception {
        String body = mockMvc.perform(post("/api/rooms")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new RoomRequest("Sala " + UUID.randomUUID(), 6))))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return UUID.fromString(objectMapper.readTree(body).path("data").path("id").asText());
    }

    private ResultActions postReservation(UUID roomId, String start, String end, boolean auth) throws Exception {
        var request = post("/api/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ReservationRequest(
                        roomId, "Ulises", Instant.parse(start), Instant.parse(end))));
        if (auth) {
            request = request.with(httpBasic("admin", "admin123"));
        }
        return mockMvc.perform(request);
    }
}