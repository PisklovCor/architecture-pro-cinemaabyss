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
    public ResponseEntity<?> createUserEvent(@RequestBody UserEvent userEvent) {
        try {
            EventResponse response = eventProducer.sendUserEvent(userEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/payment")
    public ResponseEntity<?> createPaymentEvent(@RequestBody PaymentEvent paymentEvent) {
        try {
            EventResponse response = eventProducer.sendPaymentEvent(paymentEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/movie")
    public ResponseEntity<?> createMovieEvent(@RequestBody MovieEvent movieEvent) {
        try {
            EventResponse response = eventProducer.sendMovieEvent(movieEvent);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse("Internal Server Error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/health")
    public ResponseEntity<HealthResponse> health() {
        HealthResponse healthResponse = new HealthResponse(true);
        return ResponseEntity.ok(healthResponse);
    }
}
