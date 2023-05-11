package com.solvd.micro9.users.messaging;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@DirtiesContext
@Testcontainers
public class UserProducerTest {

    private final static String TOPIC = "syncMongoElastic";

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.0")
    );

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("ticket-service", () -> "localhost:9090");
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
        registry.add("spring.kafka.producer.value-serializer", () -> JsonSerializer.class);
    }

    @Autowired
    private UserProducer producer;

    @Test
    public void verifyMessageSentToKafkaTest() {
        //given
        User user = new User("9999", "Liza", "Ya", "email@gmail.com", false);

        try (Consumer<String, User> consumer = new KafkaConsumer<>(getConsumerProps())) {
            consumer.subscribe(Collections.singleton(TOPIC));

            //when
            producer.send(user.getId(), user);

            ConsumerRecords<String, User> records = consumer.poll(Duration.ofSeconds(5));

            //then
            assertEquals(1, records.count());

            ConsumerRecord<String, User> record = records.iterator().next();
            User result = new Gson().fromJson(
                    String.valueOf(record.value()), User.class
            );
            assertEquals(user, result);
        }
    }

    private Map<String, Object> getConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, com.solvd.micro9.users.domain.aggregate.User.class);
        return props;
    }

}
