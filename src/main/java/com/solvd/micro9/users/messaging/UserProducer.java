package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.domain.elasticsearch.ESearchUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProducer implements KfProducer<String, ESearchUser> {

    @Value("${spring.kafka.producer.syncTopic}")
    private String topic;

    private final ReactiveKafkaProducerTemplate<String, ESearchUser> producer;

    @Override
    public void send(final String key, final ESearchUser value) {
        producer.send(topic, key, value)
                .subscribe();
    }

}
