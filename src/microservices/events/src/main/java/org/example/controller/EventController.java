package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.Movie;
import org.example.model.Payment;
import org.example.model.User;
import org.example.service.EventProducer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventProducer eventProducer;

    @PostMapping("/users")
    public ResponseEntity<String> createUserEvent(@RequestBody User user) {
        eventProducer.sendUserEvent(user);
        return ResponseEntity.ok("User event sent successfully");
    }

    @PostMapping("/payments")
    public ResponseEntity<String> createPaymentEvent(@RequestBody Payment payment) {
        eventProducer.sendPaymentEvent(payment);
        return ResponseEntity.ok("Payment event sent successfully");
    }

    @PostMapping("/movies")
    public ResponseEntity<String> createMovieEvent(@RequestBody Movie movie) {
        eventProducer.sendMovieEvent(movie);
        return ResponseEntity.ok("Movie event sent successfully");
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Service is healthy");
    }
}
