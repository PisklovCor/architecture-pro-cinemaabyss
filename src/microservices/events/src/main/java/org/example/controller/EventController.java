package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.model.*;
import org.example.service.EventProducer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {

    private final EventProducer eventProducer;

    @PostMapping("/user")
    public ResponseEntity<EventResponse> createUserEvent(@RequestBody UserEvent userEvent) {
            EventResponse response = eventProducer.sendUserEvent(userEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/payment")
    public ResponseEntity<?> createPaymentEvent(@RequestBody PaymentEvent paymentEvent) {
            EventResponse response = eventProducer.sendPaymentEvent(paymentEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/movie")
    public ResponseEntity<?> createMovieEvent(@RequestBody MovieEvent movieEvent) {
            EventResponse response = eventProducer.sendMovieEvent(movieEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse healthResponse = new HealthResponse(true);
        return ResponseEntity.ok(healthResponse);
    }
}
