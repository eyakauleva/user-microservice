package com.solvd.micro9.users.messaging;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KfConsumer {

    private final ReactiveKafkaConsumerTemplate<String, Long> receiver;

    @PostConstruct
    public void fetch() {
        receiver.receive()
                .subscribe(record -> {
                    System.out.println("received message: " + record);
                    record.receiverOffset().acknowledge();
                });
    }

}
