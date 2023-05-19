package com.solvd.micro9.users.integration.kafka;

import com.google.gson.Gson;
import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
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
class UserProducerIT extends KafkaTestcontainers {

    private static final String TOPIC = "syncMongoElastic";

    @Autowired
    private UserProducer producer;

    @Test
    void verifyMessageSentToKafkaTest() {
        ElstcUser user = TestUtils.getElstcUser();
        try (Consumer<String, ElstcUser> consumer = new KafkaConsumer<>(
                getConsumerProps(User.class)
        )) {
            consumer.subscribe(Collections.singleton(TOPIC));
            producer.send(user.getId(), user);
            ConsumerRecords<String, ElstcUser> records = consumer.poll(Duration.ofSeconds(5));
            ConsumerRecord<String, ElstcUser> record = records.iterator().next();
            ElstcUser result = new Gson().fromJson(
                    String.valueOf(record.value()), ElstcUser.class
            );
            Assertions.assertEquals(1, records.count());
            Assertions.assertEquals(user, result);
        }
    }

}
