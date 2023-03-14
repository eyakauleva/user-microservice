package com.solvd.micro9.users.messaging;

import com.solvd.micro9.users.web.dto.TicketDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.kafka.receiver.KafkaReceiver;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class KfConsumer {

//    private final KafkaReceiver<String, TicketDto> receiver;
//

//    private TicketDto ticketDto;
//
//    public TicketDto getTicketDto() {
//        return this.ticketDto;
//    }

    private String value;
    public String getValue(){
        return this.value;
    }

    @PostConstruct
    public void fetch() {
        receiver.receive()
                .subscribe(record -> {
                    System.out.println("received message: " + record);
                    //this.ticketDto = (TicketDto) record.value();
                    this.value = record.value();
                    record.receiverOffset().acknowledge();
                });
    }

    //private final KafkaReceiver<String, String> receiver;



    private final ReactiveKafkaConsumerTemplate<String, String> receiver;

//
//    private Flux<Object> consumeFakeConsumerDTO() {
//        return receiver
//                .receiveAutoAck()
//                .doOnNext(consumerRecord -> System.out.println(
//                        "received key=" + consumerRecord.key()
//                                + ", value=" + consumerRecord.value()
//                                + " from topic={}" + consumerRecord.topic()
//                                + ", offset=" + consumerRecord.offset())
//                )
//                .map(ConsumerRecord::value)
//                .doOnNext(fakeConsumerDTO -> System.out.println("successfully consumed }" + TicketDto.class.getSimpleName()))
//                .doOnError(throwable -> System.out.println("something bad happened while consuming : " + throwable.getMessage()));
//    }
//
//    public void fetch(){
//        consumeFakeConsumerDTO().subscribe();
//    }

}
