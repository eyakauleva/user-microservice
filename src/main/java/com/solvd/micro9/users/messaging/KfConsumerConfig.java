package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.web.dto.TicketDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class KfConsumerConfig {

//    @Bean
//    public ReceiverOptions<String, String> receiverOptions() {
//        final Map<String, Object> config = Map.of(
//                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
//                ConsumerConfig.GROUP_ID_CONFIG, "groupId",
//                //ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest",
//                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
//                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
//        );
//        final ReceiverOptions<String, String> receiverOptions = ReceiverOptions.create(config);
//        return receiverOptions.subscription(
//                        Collections.singleton("users")
//                )
//                .addAssignListener(partitions -> System.out.println("AssignListener: " + partitions))
//                .addRevokeListener(partitions -> System.out.println("RevokeListener: " + partitions));
//    }
//
//    @Bean
//    public KafkaReceiver<String, String> receiver(final ReceiverOptions<String, String> receiverOptions) {
//        return KafkaReceiver.create(receiverOptions);
//    }

    @Bean
    public ReceiverOptions<String, String> kafkaReceiverOptions(KafkaProperties kafkaProperties) {
        ReceiverOptions<String, String> basicReceiverOptions = ReceiverOptions.create(kafkaProperties.buildConsumerProperties());
        return basicReceiverOptions.subscription(Collections.singletonList("users"))
                .addAssignListener(receiverPartitions -> System.out.println("AssignListener: " + receiverPartitions))
                .addRevokeListener(receiverPartitions -> System.out.println("RevokeListener: " + receiverPartitions));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, String> reactiveKafkaConsumerTemplate(ReceiverOptions<String, String> kafkaReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(kafkaReceiverOptions);
    }

}
