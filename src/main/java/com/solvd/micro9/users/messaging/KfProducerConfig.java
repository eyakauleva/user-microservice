package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.web.dto.TicketDto;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KfProducerConfig {

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("users")
                .partitions(3)
                .replicas(1)
                .build();
    }

//    @Bean
//    public SenderOptions<String, String> senderOptions() {
//        final Map<String, Object> config = Map.of(
//                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092",
//                //ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true,
//                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
//                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class
//        );
//        return SenderOptions.create(config);
//    }
//
//    @Bean
//    public KafkaSender<String, String> sender(final SenderOptions<String, String> senderOptions) {
//        return KafkaSender.create(senderOptions);
//    }

    @Bean
    public ReactiveKafkaProducerTemplate<String, String> producerTemplate(KafkaProperties properties) {
        Map<String, Object> props = properties.buildProducerProperties();
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }

}
