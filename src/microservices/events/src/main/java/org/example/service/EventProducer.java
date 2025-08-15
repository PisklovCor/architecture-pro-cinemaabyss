package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.model.Movie;
import org.example.model.Payment;
import org.example.model.User;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventProducer {

    private static final String USER_TOPIC = "user-events";
    private static final String PAYMENT_TOPIC = "payment-events";
    private static final String MOVIE_TOPIC = "movie-events";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendUserEvent(User user) {
        log.info("Sending user event to topic {}: {}", USER_TOPIC, user);
        kafkaTemplate.send(USER_TOPIC, user.getId(), user);
    }

    public void sendPaymentEvent(Payment payment) {
        log.info("Sending payment event to topic {}: {}", PAYMENT_TOPIC, payment);
        kafkaTemplate.send(PAYMENT_TOPIC, payment.getId(), payment);
    }

    public void sendMovieEvent(Movie movie) {
        log.info("Sending movie event to topic {}: {}", MOVIE_TOPIC, movie);
        kafkaTemplate.send(MOVIE_TOPIC, movie.getId(), movie);
    }
}
