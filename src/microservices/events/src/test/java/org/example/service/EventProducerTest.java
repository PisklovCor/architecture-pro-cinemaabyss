package org.example.service;

import org.example.model.Movie;
import org.example.model.Payment;
import org.example.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventProducer eventProducer;

    @Test
    void sendUserEvent_ShouldSendToCorrectTopic() {
        // Given
        User user = new User("user-001", "John Doe", "john.doe@example.com");

        // When
        eventProducer.sendUserEvent(user);

        // Then
        verify(kafkaTemplate).send(eq("user-events"), eq("user-001"), eq(user));
    }

    @Test
    void sendPaymentEvent_ShouldSendToCorrectTopic() {
        // Given
        Payment payment = new Payment("payment-001", "user-001", new BigDecimal("99.99"), "USD");

        // When
        eventProducer.sendPaymentEvent(payment);

        // Then
        verify(kafkaTemplate).send(eq("payment-events"), eq("payment-001"), eq(payment));
    }

    @Test
    void sendMovieEvent_ShouldSendToCorrectTopic() {
        // Given
        Movie movie = new Movie("movie-001", "The Matrix", "Sci-Fi", 1999);

        // When
        eventProducer.sendMovieEvent(movie);

        // Then
        verify(kafkaTemplate).send(eq("movie-events"), eq("movie-001"), eq(movie));
    }

    @Test
    void sendUserEvent_WithNullId_ShouldStillSend() {
        // Given
        User user = new User(null, "John Doe", "john.doe@example.com");

        // When
        eventProducer.sendUserEvent(user);

        // Then
        verify(kafkaTemplate).send(eq("user-events"), eq(null), eq(user));
    }

    @Test
    void sendPaymentEvent_WithNullAmount_ShouldStillSend() {
        // Given
        Payment payment = new Payment("payment-001", "user-001", null, "USD");

        // When
        eventProducer.sendPaymentEvent(payment);

        // Then
        verify(kafkaTemplate).send(eq("payment-events"), eq("payment-001"), eq(payment));
    }
}