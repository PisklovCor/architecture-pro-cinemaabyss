package org.example.service;

import lombok.extern.slf4j.Slf4j;
import org.example.model.Movie;
import org.example.model.Payment;
import org.example.model.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EventConsumer {

    @KafkaListener(topics = "user-events", groupId = "event-processing-group")
    public void consumeUserEvent(User user) {
        log.info("Processing user event: {}", user);
        // Здесь можно добавить бизнес-логику обработки пользователя
        log.info("User event processed successfully for user: {}", user.getName());
    }

    @KafkaListener(topics = "payment-events", groupId = "event-processing-group")
    public void consumePaymentEvent(Payment payment) {
        log.info("Processing payment event: {}", payment);
        // Здесь можно добавить бизнес-логику обработки платежа
        log.info("Payment event processed successfully for amount: {} {}", payment.getAmount(), payment.getCurrency());
    }

    @KafkaListener(topics = "movie-events", groupId = "event-processing-group")
    public void consumeMovieEvent(Movie movie) {
        log.info("Processing movie event: {}", movie);
        // Здесь можно добавить бизнес-логику обработки фильма
        log.info("Movie event processed successfully for movie: {}", movie.getTitle());
    }
}
