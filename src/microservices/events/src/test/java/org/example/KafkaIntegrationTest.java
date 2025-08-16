package org.example;


import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.model.*;
import java.util.Arrays;
import java.time.LocalDateTime;

import org.example.service.EventProducer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;


import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DirtiesContext
@EmbeddedKafka(partitions = 1, topics = {"user-events", "payment-events", "movie-events"})
@ActiveProfiles("test")
class KafkaIntegrationTest {

    @Autowired
    private EventProducer eventProducer;



    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void userEventFlow_ShouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        UserEvent userEvent = new UserEvent(1, "john_doe", "john.doe@example.com", "created", LocalDateTime.now());

        // Setup consumer to capture messages
        BlockingQueue<ConsumerRecord<String, Event>> records = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties("user-events");
        containerProperties.setMessageListener((MessageListener<String, Event>) records::add);

        KafkaMessageListenerContainer<String, Event> container = createContainer(containerProperties, Event.class);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // When
        EventResponse response = eventProducer.sendUserEvent(userEvent);

        // Then
        ConsumerRecord<String, Event> received = records.poll(5, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo("1");
        assertThat(received.value().getType()).isEqualTo("user");
        assertThat(received.value().getPayload()).isInstanceOf(java.util.LinkedHashMap.class);
        assertThat(response.getStatus()).isEqualTo("success");

        container.stop();
    }

    @Test
    void paymentEventFlow_ShouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        PaymentEvent paymentEvent = new PaymentEvent(1, 1, 99.99f, "completed", LocalDateTime.now(), "card");

        // Setup consumer to capture messages
        BlockingQueue<ConsumerRecord<String, Event>> records = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties("payment-events");
        containerProperties.setMessageListener((MessageListener<String, Event>) records::add);

        KafkaMessageListenerContainer<String, Event> container = createContainer(containerProperties, Event.class);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // When
        EventResponse response = eventProducer.sendPaymentEvent(paymentEvent);

        // Then
        ConsumerRecord<String, Event> received = records.poll(5, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo("1");
        assertThat(received.value().getType()).isEqualTo("payment");
        assertThat(received.value().getPayload()).isInstanceOf(java.util.LinkedHashMap.class);
        assertThat(response.getStatus()).isEqualTo("success");

        container.stop();
    }

    @Test
    void movieEventFlow_ShouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        MovieEvent movieEvent = new MovieEvent(1, "The Matrix", "viewed", 1, 4.5f, Arrays.asList("Sci-Fi", "Action"), "Classic sci-fi movie");

        // Setup consumer to capture messages
        BlockingQueue<ConsumerRecord<String, Event>> records = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties("movie-events");
        containerProperties.setMessageListener((MessageListener<String, Event>) records::add);

        KafkaMessageListenerContainer<String, Event> container = createContainer(containerProperties, Event.class);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // When
        EventResponse response = eventProducer.sendMovieEvent(movieEvent);

        // Then
        ConsumerRecord<String, Event> received = records.poll(5, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo("1");
        assertThat(received.value().getType()).isEqualTo("movie");
        assertThat(received.value().getPayload()).isInstanceOf(java.util.LinkedHashMap.class);
        assertThat(response.getStatus()).isEqualTo("success");

        container.stop();
    }

    private <T> KafkaMessageListenerContainer<String, T> createContainer(
            ContainerProperties containerProperties, Class<T> valueType) {

        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(
                "test-group", "true", embeddedKafkaBroker);

        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProperties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType.getName());
        consumerProperties.put(JsonDeserializer.TRUSTED_PACKAGES, "org.example.model");

        DefaultKafkaConsumerFactory<String, T> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProperties);

        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }
}
