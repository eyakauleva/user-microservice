package com.solvd.micro9.users.messaging;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {"ticket-service = localhost:9090"})
@DirtiesContext
@EmbeddedKafka(partitions = 1, brokerProperties = {"listeners=PLAINTEXT://localhost:9092", "port=9092"})
public class UserProducerTest {

    private final static String TOPIC = "syncMongoElastic";

    @Autowired
    private EmbeddedKafkaBroker embeddedKafka;

    @Autowired
    private UserProducer producer;

    @Test
    public void verifyMessageSentToKafkaTest() {
        //given
        User user = new User("1111", "Liza", "Ya", "email@gmail.com", false);

        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("test-group", "true", embeddedKafka);
        try (Consumer<String, User> consumer = new KafkaConsumer<>(consumerProps)) {
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

}
