package com.yerandis.meetingRoom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// TEST DE INTEGRACIÓN — levanta el contexto completo con H2.
// Quita @Disabled de cada test cuando lo vayas a implementar.

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ReservationControllerTest {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void createReservation_whenOverlap_returns409() throws Exception {
        // Crear sala + reserva activa, luego intentar una segunda solapada → 409
    }

    @Test
    @Disabled("TODO: implementar — ver guía Paso 05")
    void cancelReservation_authenticated_returns200() throws Exception {
        // Crear sala + reserva, PATCH cancel con Basic Auth → 200 y status CANCELLED
    }
}
