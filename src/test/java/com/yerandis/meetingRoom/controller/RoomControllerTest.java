package com.yerandis.meetingRoom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yerandis.meetingRoom.dto.request.RoomRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoomControllerTest {

    @Autowired MockMvc      mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    void createRoom_withValidData_returns201() throws Exception {
        String name = "Sala " + UUID.randomUUID();
        mockMvc.perform(post("/api/rooms")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomRequest(name, 8))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value(name));
    }

    @Test
    void createRoom_withBlankName_returns400() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomRequest("  ", 5))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data.name").exists());
    }

    @Test
    void createRoom_withoutAuth_returns401() throws Exception {
        mockMvc.perform(post("/api/rooms")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new RoomRequest("Sala X", 5))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void listRooms_withoutAuth_returns200() throws Exception {
        mockMvc.perform(get("/api/rooms"))
                .andExpect(status().isOk());
    }
}