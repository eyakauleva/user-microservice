package com.solvd.micro9.users.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.domain.es.EsUser;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@DirtiesContext
@Testcontainers
public class EsProducerTest {

    private final static String TOPIC = "users";

    @Container
    public static KafkaContainer kafkaContainer = new KafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.3.0")
    );

    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("ticket-service", () -> "localhost:9090");
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @Autowired
    private EsProducer producer;

    @Test
    @SneakyThrows
    public void verifyMessageSentToKafkaTest() {
        //given
        User user = new User("1111", "Liza", "Ya", "email@gmail.com", false);
        String payload = new Gson().toJson(user);
        Es event = EsUser.builder()
                .type(EsType.USER_CREATED)
                .time(LocalDateTime.now())
                .createdBy("Liza")
                .entityId(UUID.randomUUID().toString())
                .payload(payload)
                .status(EsStatus.SUBMITTED)
                .build();

        try (Consumer<String, Es> consumer = new KafkaConsumer<>(getConsumerProps())) {
            consumer.subscribe(Collections.singleton(TOPIC));

            //when
            producer.send(event.getType().toString(), event);

            ConsumerRecords<String, Es> records = consumer.poll(Duration.ofSeconds(5));

            //then
            assertEquals(1, records.count());

            ConsumerRecord<String, Es> record = records.iterator().next();

            ObjectMapper mapper = new ObjectMapper();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            mapper.registerModule(new JavaTimeModule());
            mapper.setDateFormat(dateFormat);

            Es result = mapper.readValue(String.valueOf(record.value()), EsUser.class);
            assertEquals(event, result);
        }
    }

    private Map<String, Object> getConsumerProps() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, com.solvd.micro9.users.domain.es.Es.class);
        return props;
    }

}
