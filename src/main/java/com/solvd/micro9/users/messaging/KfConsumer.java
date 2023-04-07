package com.solvd.micro9.users.messaging;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.Ticket;
import com.solvd.micro9.users.domain.command.CompleteTransactionCommand;
import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KfConsumer {

    private final ReactiveKafkaConsumerTemplate<String, Es> receiver;
    private final EsUserCommandHandler commandHandler;

    @PostConstruct
    public void fetch() {
        receiver.receive()
                .subscribe(record -> {
                    if (EsType.TICKET_USER_DELETED.equals(record.value().getType())
                            && !EsStatus.PENDING.equals(record.value().getStatus())) {
                        log.info("received value: {}", record.value());
                        Ticket ticket = new Gson().fromJson(record.value().getPayload(), Ticket.class);
                        CompleteTransactionCommand command = new CompleteTransactionCommand(
                                ticket.getUserId(),
                                record.value().getStatus()
                        );
                        commandHandler.apply(command);
                    }
                    record.receiverOffset().acknowledge();
                });
    }

}
