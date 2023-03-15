package com.solvd.micro9.users.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KfProducer {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final ReactiveKafkaProducerTemplate<String, Long> producer;

    public void send(String key, Long value) {
        producer.send(topic, key, value)
                .subscribe();
    }

}