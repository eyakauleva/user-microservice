package com.solvd.micro9.users.messaging;

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
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {"ticket-service = localhost:9090"})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class EsProducerTest {

    private final static String TOPIC = "users";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

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

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        consumerProps.put(JsonDeserializer.VALUE_DEFAULT_TYPE, com.solvd.micro9.users.domain.es.Es.class);
        try (Consumer<String, Es> consumer = new KafkaConsumer<>(consumerProps)) {
            consumer.subscribe(Collections.singleton(TOPIC));

            //when
            producer.send(event.getType().toString(), event);

            ConsumerRecords<String, Es> records = consumer.poll(Duration.ofSeconds(5));

            //then
            assertEquals(1, records.count());

            ConsumerRecord<String, Es> record = records.iterator().next();
            assertEquals(event.toString(), record.value().toString());
        }
    }

}
