package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.*;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducer {

    private static final String USER_TOPIC = "user-events";
    private static final String PAYMENT_TOPIC = "payment-events";
    private static final String MOVIE_TOPIC = "movie-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public EventResponse sendUserEvent(UserEvent userEvent) {
        String eventId = generateEventId("user", userEvent.getUserId());
        Event event = new Event(eventId, "user", LocalDateTime.now(), userEvent);
        
        log.info("Sending user event to topic {}: {}", USER_TOPIC, userEvent);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(USER_TOPIC, userEvent.getUserId().toString(), event);
            SendResult<String, Object> result = future.get();
            
            return new EventResponse(
                "success",
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset(),
                event
            );
        } catch (Exception e) {
            log.error("Failed to send user event", e);
            throw new RuntimeException("Failed to send user event", e);
        }
    }

    public EventResponse sendPaymentEvent(PaymentEvent paymentEvent) {
        String eventId = generateEventId("payment", paymentEvent.getPaymentId());
        Event event = new Event(eventId, "payment", LocalDateTime.now(), paymentEvent);
        
        log.info("Sending payment event to topic {}: {}", PAYMENT_TOPIC, paymentEvent);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(PAYMENT_TOPIC, paymentEvent.getPaymentId().toString(), event);
            SendResult<String, Object> result = future.get();
            
            return new EventResponse(
                "success",
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset(),
                event
            );
        } catch (Exception e) {
            log.error("Failed to send payment event", e);
            throw new RuntimeException("Failed to send payment event", e);
        }
    }

    public EventResponse sendMovieEvent(MovieEvent movieEvent) {
        String eventId = generateEventId("movie", movieEvent.getMovieId());
        Event event = new Event(eventId, "movie", LocalDateTime.now(), movieEvent);
        
        log.info("Sending movie event to topic {}: {}", MOVIE_TOPIC, movieEvent);
        
        try {
            CompletableFuture<SendResult<String, Object>> future = 
                kafkaTemplate.send(MOVIE_TOPIC, movieEvent.getMovieId().toString(), event);
            SendResult<String, Object> result = future.get();
            
            return new EventResponse(
                "success",
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset(),
                event
            );
        } catch (Exception e) {
            log.error("Failed to send movie event", e);
            throw new RuntimeException("Failed to send movie event", e);
        }
    }

    private String generateEventId(String eventType, Integer entityId) {
        return eventType + "-" + entityId + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
}
