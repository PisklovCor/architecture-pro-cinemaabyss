package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Event;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventConsumer {

    @KafkaListener(topics = "user-events", groupId = "event-processing-group")
    public void consumeUserEvent(Event event) {
        log.info("Processing user event: {}", event);
        // Здесь можно добавить бизнес-логику обработки событий пользователя
        log.info("User event processed successfully with id: {}", event.getId());
    }

    @KafkaListener(topics = "payment-events", groupId = "event-processing-group")
    public void consumePaymentEvent(Event event) {
        log.info("Processing payment event: {}", event);
        // Здесь можно добавить бизнес-логику обработки событий платежа
        log.info("Payment event processed successfully with id: {}", event.getId());
    }

    @KafkaListener(topics = "movie-events", groupId = "event-processing-group")
    public void consumeMovieEvent(Event event) {
        log.info("Processing movie event: {}", event);
        // Здесь можно добавить бизнес-логику обработки событий фильма
        log.info("Movie event processed successfully with id: {}", event.getId());
    }
}
