package com.solvd.micro9.users.messaging;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Configuration
public class KfConsumerConfig {

    @Bean
    public ReceiverOptions<String, Long> receiverOptions(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, Long> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList("users"))
                .addAssignListener(receiverPartitions -> System.out.println("AssignListener: " + receiverPartitions))
                .addRevokeListener(receiverPartitions -> System.out.println("RevokeListener: " + receiverPartitions));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Long> consumer(ReceiverOptions<String, Long> receiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

}
