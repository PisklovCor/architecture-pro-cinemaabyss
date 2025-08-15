package org.example.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.model.Movie;
import org.example.model.Payment;
import org.example.model.User;
import org.example.service.EventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
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
                .andExpect(content().string("Service is healthy"));
    }

    @Test
    void createUserEvent_ShouldReturnSuccessMessage() throws Exception {
        // Given
        User user = new User("user-001", "John Doe", "john.doe@example.com");
        doNothing().when(eventProducer).sendUserEvent(any(User.class));

        // When & Then
        mockMvc.perform(post("/api/events/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(content().string("User event sent successfully"));

        verify(eventProducer).sendUserEvent(any(User.class));
    }

    @Test
    void createPaymentEvent_ShouldReturnSuccessMessage() throws Exception {
        // Given
        Payment payment = new Payment("payment-001", "user-001", new BigDecimal("99.99"), "USD");
        doNothing().when(eventProducer).sendPaymentEvent(any(Payment.class));

        // When & Then
        mockMvc.perform(post("/api/events/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment event sent successfully"));

        verify(eventProducer).sendPaymentEvent(any(Payment.class));
    }

    @Test
    void createMovieEvent_ShouldReturnSuccessMessage() throws Exception {
        // Given
        Movie movie = new Movie("movie-001", "The Matrix", "Sci-Fi", 1999);
        doNothing().when(eventProducer).sendMovieEvent(any(Movie.class));

        // When & Then
        mockMvc.perform(post("/api/events/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(movie)))
                .andExpect(status().isOk())
                .andExpect(content().string("Movie event sent successfully"));

        verify(eventProducer).sendMovieEvent(any(Movie.class));
    }

    @Test
    void createUserEvent_WithInvalidJson_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/events/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("invalid json"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createUserEvent_WithEmptyBody_ShouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/events/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createPaymentEvent_WithNullAmount_ShouldStillWork() throws Exception {
        // Given
        Payment payment = new Payment("payment-001", "user-001", null, "USD");
        doNothing().when(eventProducer).sendPaymentEvent(any(Payment.class));

        // When & Then
        mockMvc.perform(post("/api/events/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payment)))
                .andExpect(status().isOk())
                .andExpect(content().string("Payment event sent successfully"));

        verify(eventProducer).sendPaymentEvent(any(Payment.class));
    }
}