package com.yerandis.meetingRoom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yerandis.meetingRoom.dto.request.RoomRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TEST DE INTEGRACIÓN — levanta el contexto completo con H2.
// Quita @Disabled de cada test cuando lo vayas a implementar.

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RoomControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void createRoom_withValidData_returns201() throws Exception {
        // POST /api/rooms con Basic Auth → 201
        // Verificar que data.name coincide
    }

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void createRoom_withBlankName_returns400() throws Exception {
        // POST /api/rooms con name en blanco → 400
    }

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void listRooms_withoutAuth_returns200() throws Exception {
        // GET /api/rooms sin credenciales → 200 (endpoint público)
    }
}
