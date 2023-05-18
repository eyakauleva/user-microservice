package com.solvd.micro9.users.service.impl;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.command.CompleteTransactionCommand;
import com.solvd.micro9.users.domain.command.CreateUserCommand;
import com.solvd.micro9.users.domain.command.DeleteUserCommand;
import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.messaging.KfProducer;
import com.solvd.micro9.users.persistence.eventstore.EsUserRepository;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import com.solvd.micro9.users.service.cache.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Service
@Slf4j
public class EsUserCommandHandlerImpl implements EsUserCommandHandler {

    private final EsUserRepository esUserRepository;
    private final KfProducer<String, Es> producer;
    private final ReactiveHashOperations<String, String, User> cache;
    private final Map<EsType, Function<Es, Mono<?>>> dbSynchronizerHandler;

    @Autowired
    public EsUserCommandHandlerImpl(final EsUserRepository esUserRepository,
                                    final KfProducer<String, Es> producer,
                                    final ReactiveRedisOperations<String, User> operations,
                                    final Map<EsType, Function<Es, Mono<?>>>
                                            dbSynchronizerHandler) {
        this.esUserRepository = esUserRepository;
        this.producer = producer;
        this.cache = operations.opsForHash();
        this.dbSynchronizerHandler = dbSynchronizerHandler;
    }

    @Override
    public Mono<EsUser> apply(final CreateUserCommand command) {
        String payload = new Gson().toJson(command.getUser());
        EsUser event = EsUser.builder()
                .type(EsType.USER_CREATED)
                .time(LocalDateTime.now())
                .createdBy(command.getCommandBy())
                .entityId(UUID.randomUUID().toString())
                .payload(payload)
                .status(EsStatus.SUBMITTED)
                .build();
        return esUserRepository.save(event)
                .doOnNext(createdEvent -> {
                    command.getUser().setId(
                            createdEvent.getEntityId()
                    );
                    cache.put(
                                    RedisConfig.CACHE_KEY,
                                    createdEvent.getEntityId(),
                                    command.getUser()
                            )
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                    dbSynchronizerHandler.get(createdEvent.getType()).apply(createdEvent)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                });
    }

    @Override
    public Mono<EsUser> apply(final DeleteUserCommand command) {
        EsUser event = EsUser.builder()
                .type(EsType.USER_DELETED)
                .time(LocalDateTime.now())
                .createdBy(command.getCommandBy())
                .entityId(command.getId())
                .status(EsStatus.PENDING)
                .build();
        return esUserRepository.save(event)
                .doOnNext(createdEvent -> producer.send(EsType.USER_DELETED.toString(),
                        createdEvent));
    }

    @Override
    public Flux<EsUser> apply(final CompleteTransactionCommand command) {
        return esUserRepository.findByEntityIdTypeStatus(
                        command.getUserId(),
                        EsType.USER_DELETED,
                        EsStatus.PENDING
                )
                .flatMap(esUser -> {
                    EsUser completeEvent = EsUser.builder()
                            .type(EsType.USER_DELETED)
                            .time(LocalDateTime.now())
                            .createdBy(esUser.getCreatedBy())
                            .entityId(command.getUserId())
                            .status(command.getStatus())
                            .build();
                    return esUserRepository.save(completeEvent);
                })
                .map(event -> {
                    cache
                            .remove(
                                    RedisConfig.CACHE_KEY,
                                    event.getEntityId()
                            )
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                    return event;
                })
                .map(event -> {
                    producer.send(
                            EsType.USER_DELETED.toString(),
                            event
                    );
                    return event;
                })
                .map(event -> {
                    dbSynchronizerHandler.get(event.getType())
                            .apply(event)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                    return event;
                });
    }

}
