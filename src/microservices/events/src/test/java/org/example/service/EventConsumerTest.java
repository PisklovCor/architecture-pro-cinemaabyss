package org.example.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.example.model.Movie;
import org.example.model.Payment;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
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
        User user = new User("user-001", "John Doe", "john.doe@example.com");

        // When
        eventConsumer.consumeUserEvent(user);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(0).getFormattedMessage()).contains("Processing user event: User{id='user-001'");
        assertThat(logsList.get(1).getFormattedMessage()).contains("User event processed successfully for user: John Doe");
    }

    @Test
    void consumePaymentEvent_ShouldLogProcessingMessage() {
        // Given
        Payment payment = new Payment("payment-001", "user-001", new BigDecimal("99.99"), "USD");

        // When
        eventConsumer.consumePaymentEvent(payment);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(0).getFormattedMessage()).contains("Processing payment event: Payment{id='payment-001'");
        assertThat(logsList.get(1).getFormattedMessage()).contains("Payment event processed successfully for amount: 99.99 USD");
    }

    @Test
    void consumeMovieEvent_ShouldLogProcessingMessage() {
        // Given
        Movie movie = new Movie("movie-001", "The Matrix", "Sci-Fi", 1999);

        // When
        eventConsumer.consumeMovieEvent(movie);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(0).getFormattedMessage()).contains("Processing movie event: Movie{id='movie-001'");
        assertThat(logsList.get(1).getFormattedMessage()).contains("Movie event processed successfully for movie: The Matrix");
    }

    @Test
    void consumeUserEvent_WithNullName_ShouldStillProcess() {
        // Given
        User user = new User("user-001", null, "john.doe@example.com");

        // When
        eventConsumer.consumeUserEvent(user);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(1).getFormattedMessage()).contains("User event processed successfully for user: null");
    }

    @Test
    void consumePaymentEvent_WithNullAmount_ShouldStillProcess() {
        // Given
        Payment payment = new Payment("payment-001", "user-001", null, "USD");

        // When
        eventConsumer.consumePaymentEvent(payment);

        // Then
        List<ILoggingEvent> logsList = listAppender.list;
        assertThat(logsList).hasSize(2);
        assertThat(logsList.get(1).getFormattedMessage()).contains("Payment event processed successfully for amount: null USD");
    }
}