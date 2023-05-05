package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.domain.es.Es;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;

@Slf4j
@Configuration
public class KfConsumerConfig {

    @Value("${spring.kafka.consumer.topic}")
    private String topic;

    @Bean
    public ReceiverOptions<String, Es> receiverOptions(
            final KafkaProperties kafkaProperties
    ) {
        ReceiverOptions<String, Es> basicReceiverOptions =
                ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList(topic))
                .addAssignListener(receiverPartitions ->
                        log.info("AssignListener: {}", receiverPartitions))
                .addRevokeListener(receiverPartitions ->
                        log.info("RevokeListener: {}", receiverPartitions));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, Es> consumer(
            final ReceiverOptions<String, Es> receiverOptions
    ) {
        return new ReactiveKafkaConsumerTemplate<>(receiverOptions);
    }

}
