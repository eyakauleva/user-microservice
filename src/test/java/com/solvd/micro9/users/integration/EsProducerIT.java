package com.solvd.micro9.users.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.messaging.EsProducer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.UUID;

@Slf4j
@SpringBootTest
@DirtiesContext
class EsProducerIT extends TestcontainersTest {

    private static final String TOPIC = "users";

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EsProducer producer;

    @Test
    @SneakyThrows
    void verifyMessageSentToKafkaTest() {
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
        try (Consumer<String, Es> consumer = new KafkaConsumer<>(
                getConsumerProps(Es.class)
        )) {
            consumer.subscribe(Collections.singleton(TOPIC));
            producer.send(event.getType().toString(), event);
            ConsumerRecords<String, Es> records = consumer.poll(Duration.ofSeconds(5));
            ConsumerRecord<String, Es> record = records.iterator().next();
            Es result = objectMapper.readValue(
                    String.valueOf(record.value()), EsUser.class
            );
            Assertions.assertEquals(1, records.count());
            Assertions.assertEquals(event, result);
        }
    }

}
