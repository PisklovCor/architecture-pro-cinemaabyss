package org.example.service;

import org.example.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventProducerTest {

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @InjectMocks
    private EventProducer eventProducer;

    @Test
    void sendUserEvent_ShouldReturnEventResponse() throws Exception {
        // Given
        UserEvent userEvent = new UserEvent(1, "john_doe", "john.doe@example.com", "registered", LocalDateTime.now());
        
        SendResult<String, Object> sendResult = createMockSendResult(0, 42L);
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(eq("user-events"), eq("1"), any(Event.class))).thenReturn(future);

        // When
        EventResponse response = eventProducer.sendUserEvent(userEvent);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getPartition()).isEqualTo(0);
        assertThat(response.getOffset()).isEqualTo(42L);
        assertThat(response.getEvent().getType()).isEqualTo("user");
        assertThat(response.getEvent().getPayload()).isEqualTo(userEvent);
    }

    @Test
    void sendPaymentEvent_ShouldReturnEventResponse() throws Exception {
        // Given
        PaymentEvent paymentEvent = new PaymentEvent(1, 1, 99.99f, "completed", LocalDateTime.now(), "credit_card");
        
        SendResult<String, Object> sendResult = createMockSendResult(0, 43L);
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(eq("payment-events"), eq("1"), any(Event.class))).thenReturn(future);

        // When
        EventResponse response = eventProducer.sendPaymentEvent(paymentEvent);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getPartition()).isEqualTo(0);
        assertThat(response.getOffset()).isEqualTo(43L);
        assertThat(response.getEvent().getType()).isEqualTo("payment");
        assertThat(response.getEvent().getPayload()).isEqualTo(paymentEvent);
    }

    @Test
    void sendMovieEvent_ShouldReturnEventResponse() throws Exception {
        // Given
        MovieEvent movieEvent = new MovieEvent(1, "Inception", "viewed", 1, 8.8f, 
            Arrays.asList("Sci-Fi", "Action", "Thriller"), "A mind-bending thriller");
        
        SendResult<String, Object> sendResult = createMockSendResult(0, 44L);
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(eq("movie-events"), eq("1"), any(Event.class))).thenReturn(future);

        // When
        EventResponse response = eventProducer.sendMovieEvent(movieEvent);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getPartition()).isEqualTo(0);
        assertThat(response.getOffset()).isEqualTo(44L);
        assertThat(response.getEvent().getType()).isEqualTo("movie");
        assertThat(response.getEvent().getPayload()).isEqualTo(movieEvent);
    }

    @Test
    void sendUserEvent_WhenKafkaFails_ShouldThrowException() {
        // Given
        UserEvent userEvent = new UserEvent(1, "john_doe", "john.doe@example.com", "registered", LocalDateTime.now());
        
        CompletableFuture<SendResult<String, Object>> future = new CompletableFuture<>();
        future.completeExceptionally(new RuntimeException("Kafka connection failed"));
        
        when(kafkaTemplate.send(eq("user-events"), eq("1"), any(Event.class))).thenReturn(future);

        // When & Then
        assertThatThrownBy(() -> eventProducer.sendUserEvent(userEvent))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Failed to send user event");
    }

    @Test
    void sendPaymentEvent_WithNullAmount_ShouldStillWork() throws Exception {
        // Given
        PaymentEvent paymentEvent = new PaymentEvent(1, 1, null, "completed", LocalDateTime.now(), "credit_card");
        
        SendResult<String, Object> sendResult = createMockSendResult(0, 45L);
        CompletableFuture<SendResult<String, Object>> future = CompletableFuture.completedFuture(sendResult);
        
        when(kafkaTemplate.send(eq("payment-events"), eq("1"), any(Event.class))).thenReturn(future);

        // When
        EventResponse response = eventProducer.sendPaymentEvent(paymentEvent);

        // Then
        assertThat(response.getStatus()).isEqualTo("success");
        assertThat(response.getEvent().getPayload()).isEqualTo(paymentEvent);
    }

    private SendResult<String, Object> createMockSendResult(int partition, long offset) {
        SendResult<String, Object> sendResult = org.mockito.Mockito.mock(SendResult.class);
        org.apache.kafka.clients.producer.RecordMetadata metadata = 
            org.mockito.Mockito.mock(org.apache.kafka.clients.producer.RecordMetadata.class);
        
        when(sendResult.getRecordMetadata()).thenReturn(metadata);
        when(metadata.partition()).thenReturn(partition);
        when(metadata.offset()).thenReturn(offset);
        
        return sendResult;
    }
}