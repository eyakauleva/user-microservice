package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.web.dto.TicketDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderRecord;

@Component
@RequiredArgsConstructor
public class KfProducer {

//    private final KafkaSender<String, String> sender;
//
//    public void send(String key, String value) {
//        sender.send(
//                Mono.just(SenderRecord.create(
//                        "users",
//                        1,
//                        System.currentTimeMillis(),
//                        key,
//                        value,
//                        null
//                ))
//        ).subscribe();
//    }

    private final ReactiveKafkaProducerTemplate<String, String> producerTemplate;
    private String topic = "users";

    public void send(String key, String value) {
        producerTemplate.send(topic, key, value)
                .subscribe();
    }

}
