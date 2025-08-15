package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.example.model.Movie;
import org.example.model.Payment;
import org.example.model.User;
import org.example.service.EventConsumer;
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

import java.math.BigDecimal;
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
    private EventConsumer eventConsumer;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Test
    void userEventFlow_ShouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        User user = new User("user-001", "John Doe", "john.doe@example.com");

        // Setup consumer to capture messages
        BlockingQueue<ConsumerRecord<String, User>> records = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties("user-events");
        containerProperties.setMessageListener((MessageListener<String, User>) records::add);

        KafkaMessageListenerContainer<String, User> container = createContainer(containerProperties, User.class);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // When
        eventProducer.sendUserEvent(user);

        // Then
        ConsumerRecord<String, User> received = records.poll(5, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo("user-001");
        assertThat(received.value().getId()).isEqualTo("user-001");
        assertThat(received.value().getName()).isEqualTo("John Doe");
        assertThat(received.value().getEmail()).isEqualTo("john.doe@example.com");

        container.stop();
    }

    @Test
    void paymentEventFlow_ShouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        Payment payment = new Payment("payment-001", "user-001", new BigDecimal("99.99"), "USD");

        // Setup consumer to capture messages
        BlockingQueue<ConsumerRecord<String, Payment>> records = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties("payment-events");
        containerProperties.setMessageListener((MessageListener<String, Payment>) records::add);

        KafkaMessageListenerContainer<String, Payment> container = createContainer(containerProperties, Payment.class);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // When
        eventProducer.sendPaymentEvent(payment);

        // Then
        ConsumerRecord<String, Payment> received = records.poll(5, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo("payment-001");
        assertThat(received.value().getId()).isEqualTo("payment-001");
        assertThat(received.value().getUserId()).isEqualTo("user-001");
        assertThat(received.value().getAmount()).isEqualTo(new BigDecimal("99.99"));
        assertThat(received.value().getCurrency()).isEqualTo("USD");

        container.stop();
    }

    @Test
    void movieEventFlow_ShouldSendAndReceiveMessage() throws InterruptedException {
        // Given
        Movie movie = new Movie("movie-001", "The Matrix", "Sci-Fi", 1999);

        // Setup consumer to capture messages
        BlockingQueue<ConsumerRecord<String, Movie>> records = new LinkedBlockingQueue<>();
        ContainerProperties containerProperties = new ContainerProperties("movie-events");
        containerProperties.setMessageListener((MessageListener<String, Movie>) records::add);

        KafkaMessageListenerContainer<String, Movie> container = createContainer(containerProperties, Movie.class);
        container.start();
        ContainerTestUtils.waitForAssignment(container, embeddedKafkaBroker.getPartitionsPerTopic());

        // When
        eventProducer.sendMovieEvent(movie);

        // Then
        ConsumerRecord<String, Movie> received = records.poll(5, TimeUnit.SECONDS);
        assertThat(received).isNotNull();
        assertThat(received.key()).isEqualTo("movie-001");
        assertThat(received.value().getId()).isEqualTo("movie-001");
        assertThat(received.value().getTitle()).isEqualTo("The Matrix");
        assertThat(received.value().getGenre()).isEqualTo("Sci-Fi");
        assertThat(received.value().getYear()).isEqualTo(1999);

        container.stop();
    }

    private <T> KafkaMessageListenerContainer<String, T> createContainer(
            ContainerProperties containerProperties, Class<T> valueType) {

        Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps(
                "test-group", "true", embeddedKafkaBroker);

        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProperties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, valueType.getName());
        consumerProperties.put(JsonDeserializer.TRUSTED_PACKAGES, "com.example.kafkaservice.model");

        DefaultKafkaConsumerFactory<String, T> consumerFactory =
                new DefaultKafkaConsumerFactory<>(consumerProperties);

        return new KafkaMessageListenerContainer<>(consumerFactory, containerProperties);
    }
}
