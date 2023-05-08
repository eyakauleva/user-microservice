package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.Es;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KfProducer {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Value("${spring.kafka.producer.syncTopic}")
    private String syncTopic;

    private final ReactiveKafkaProducerTemplate<String, Es> producer;

    private final ReactiveKafkaProducerTemplate<String, User> syncProducer;
    
    public void send(final String key, final Es value) {
        producer.send(topic, key, value)
                .subscribe();
    }

    public void send(final String key, final User value) {
        syncProducer.send(syncTopic, key, value)
                .subscribe();
    }

}
