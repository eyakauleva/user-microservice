package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.domain.es.Es;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class KfProducerConfig {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Value("${spring.kafka.partitions}")
    private int partitionCount;

    @Value("${spring.kafka.replicas}")
    private int replicaCount;

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name(topic)
                .partitions(partitionCount)
                .replicas(replicaCount)
                .build();
    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, Es> producer(
            final KafkaProperties properties
    ) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }

}
