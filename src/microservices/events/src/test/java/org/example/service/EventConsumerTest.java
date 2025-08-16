package org.example.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.example.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EventConsumerTest {

    private EventConsumer eventConsumer;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        eventConsumer = new EventConsumer();

        // Setup logging capture
        Logger logger = (Logger) LoggerFactory.getLogger(EventConsumer.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void consumeUserEvent_ShouldLogProcessingMessage() {
        // Given
        UserEvent userEvent = new UserEvent(1, "john_doe", "john.doe@example.com", "registered", LocalDateTime.now());
        Event event = new Event("user-1-abc123", "user", LocalDateTime.now(), userEvent);

        // When
        eventConsumer.consumeUserEvent(event);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(0).getFormattedMessage()).contains("Processing user event: Event{id='user-1-abc123'");
        assertThat(logsList.get(1).getFormattedMessage()).contains("User event processed successfully with id: user-1-abc123");
    }

    @Test
    void consumePaymentEvent_ShouldLogProcessingMessage() {
        // Given
        PaymentEvent paymentEvent = new PaymentEvent(1, 1, 99.99f, "completed", LocalDateTime.now(), "credit_card");
        Event event = new Event("payment-1-abc123", "payment", LocalDateTime.now(), paymentEvent);

        // When
        eventConsumer.consumePaymentEvent(event);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(0).getFormattedMessage()).contains("Processing payment event: Event{id='payment-1-abc123'");
        assertThat(logsList.get(1).getFormattedMessage()).contains("Payment event processed successfully with id: payment-1-abc123");
    }

    @Test
    void consumeMovieEvent_ShouldLogProcessingMessage() {
        // Given
        MovieEvent movieEvent = new MovieEvent(1, "Inception", "viewed", 1, 8.8f, 
            Arrays.asList("Sci-Fi", "Action", "Thriller"), "A mind-bending thriller");
        Event event = new Event("movie-1-abc123", "movie", LocalDateTime.now(), movieEvent);

        // When
        eventConsumer.consumeMovieEvent(event);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(0).getFormattedMessage()).contains("Processing movie event: Event{id='movie-1-abc123'");
        assertThat(logsList.get(1).getFormattedMessage()).contains("Movie event processed successfully with id: movie-1-abc123");
    }

    @Test
    void consumeUserEvent_WithNullEventId_ShouldStillProcess() {
        // Given
        UserEvent userEvent = new UserEvent(1, null, "john.doe@example.com", "registered", LocalDateTime.now());
        Event event = new Event(null, "user", LocalDateTime.now(), userEvent);

        // When
        eventConsumer.consumeUserEvent(event);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(1).getFormattedMessage()).contains("User event processed successfully with id: null");
    }

    @Test
    void consumePaymentEvent_WithNullAmount_ShouldStillProcess() {
        // Given
        PaymentEvent paymentEvent = new PaymentEvent(1, 1, null, "completed", LocalDateTime.now(), "credit_card");
        Event event = new Event("payment-1-abc123", "payment", LocalDateTime.now(), paymentEvent);

        // When
        eventConsumer.consumePaymentEvent(event);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(1).getFormattedMessage()).contains("Payment event processed successfully with id: payment-1-abc123");
    }
}