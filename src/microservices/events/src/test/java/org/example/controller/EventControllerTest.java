package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.*;
import org.example.service.EventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EventProducer eventProducer;

    @Test
    void healthCheck_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/events/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void createUserEvent_ShouldReturnEventResponse() throws Exception {
        // Given
        UserEvent userEvent = new UserEvent(1, "john_doe", "john.doe@example.com", "registered", LocalDateTime.now());
        EventResponse expectedResponse = new EventResponse("success", 0, 42L, 
            new Event("user-1-abc123", "user", LocalDateTime.now(), userEvent));
        
        when(eventProducer.sendUserEvent(any(UserEvent.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/events/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.partition").value(0))
                .andExpect(jsonPath("$.offset").value(42));
    }

    @Test
    void createPaymentEvent_ShouldReturnEventResponse() throws Exception {
        // Given
        PaymentEvent paymentEvent = new PaymentEvent(1, 1, 99.99f, "completed", LocalDateTime.now(), "credit_card");
        EventResponse expectedResponse = new EventResponse("success", 0, 43L, 
            new Event("payment-1-abc123", "payment", LocalDateTime.now(), paymentEvent));
        
        when(eventProducer.sendPaymentEvent(any(PaymentEvent.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/events/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.partition").value(0))
                .andExpect(jsonPath("$.offset").value(43));
    }

    @Test
    void createMovieEvent_ShouldReturnEventResponse() throws Exception {
        // Given
        MovieEvent movieEvent = new MovieEvent(1, "Inception", "viewed", 1, 8.8f, 
            Arrays.asList("Sci-Fi", "Action", "Thriller"), "A mind-bending thriller");
        EventResponse expectedResponse = new EventResponse("success", 0, 44L, 
            new Event("movie-1-abc123", "movie", LocalDateTime.now(), movieEvent));
        
        when(eventProducer.sendMovieEvent(any(MovieEvent.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/events/movie")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movieEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.partition").value(0))
                .andExpect(jsonPath("$.offset").value(44));
    }

    @Test
    void createUserEvent_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/events/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserEvent_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/events/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserEvent_WhenProducerThrowsException_ShouldReturnInternalServerError() throws Exception {
        // Given
        UserEvent userEvent = new UserEvent(1, "john_doe", "john.doe@example.com", "registered", LocalDateTime.now());
        when(eventProducer.sendUserEvent(any(UserEvent.class))).thenThrow(new RuntimeException("Kafka error"));

        // When & Then
        mockMvc.perform(post("/api/events/user")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userEvent)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"));
    }

    @Test
    void createPaymentEvent_WithNullAmount_ShouldStillWork() throws Exception {
        // Given
        PaymentEvent paymentEvent = new PaymentEvent(1, 1, null, "completed", LocalDateTime.now(), "credit_card");
        EventResponse expectedResponse = new EventResponse("success", 0, 45L, 
            new Event("payment-1-abc123", "payment", LocalDateTime.now(), paymentEvent));
        
        when(eventProducer.sendPaymentEvent(any(PaymentEvent.class))).thenReturn(expectedResponse);

        // When & Then
        mockMvc.perform(post("/api/events/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(paymentEvent)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("success"));
    }
}