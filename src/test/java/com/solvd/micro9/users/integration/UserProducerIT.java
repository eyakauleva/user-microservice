package com.solvd.micro9.users.integration;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.messaging.UserProducer;
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
import java.util.Collections;

@Slf4j
@SpringBootTest
@DirtiesContext
public class UserProducerIT extends TestcontainersTest {

    private static final String TOPIC = "syncMongoElastic";

    @Autowired
    private UserProducer producer;

    @Test
    public void verifyMessageSentToKafkaTest() {
        User user = new User("1111", "Liza", "Ya", "email@gmail.com",
                "+12345", 20, Gender.FEMALE, 170.5f,
                50.2f, EyesColor.BLUE, false);
        try (Consumer<String, User> consumer = new KafkaConsumer<>(
                getConsumerProps(User.class)
        )) {
            consumer.subscribe(Collections.singleton(TOPIC));
            producer.send(user.getId(), user);
            ConsumerRecords<String, User> records = consumer.poll(Duration.ofSeconds(5));
            ConsumerRecord<String, User> record = records.iterator().next();
            User result = new Gson().fromJson(
                    String.valueOf(record.value()), User.class
            );
            Assertions.assertEquals(1, records.count());
            Assertions.assertEquals(user, result);
        }
    }

}
