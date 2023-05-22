package com.solvd.micro9.users.integration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@Testcontainers
abstract class KafkaTestcontainers {

    @Container
    private static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.0")
    );

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("ticket-service", () -> "localhost:9090");
        registry.add("spring.kafka.bootstrap-servers",
                KAFKA_CONTAINER::getBootstrapServers
        );
    }

    Map<String, Object> getConsumerProps(final Class<?> clazz) {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                KAFKA_CONTAINER.getBootstrapServers()
        );
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class
        );
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, clazz);
        return props;
    }

}
