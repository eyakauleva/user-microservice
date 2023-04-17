package com.solvd.micro9.users.service.impl;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.command.CompleteTransactionCommand;
import com.solvd.micro9.users.domain.command.CreateUserCommand;
import com.solvd.micro9.users.domain.command.DeleteUserCommand;
import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.messaging.KfProducer;
import com.solvd.micro9.users.persistence.eventstore.EsUserRepository;
import com.solvd.micro9.users.service.DbSynchronizer;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import com.solvd.micro9.users.service.cache.RedisConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class EsUserCommandHandlerImpl implements EsUserCommandHandler {

    private final EsUserRepository esUserRepository;
    private final KfProducer producer;
    private final ReactiveHashOperations<String, String, User> cache;
    private final DbSynchronizer synchronizer;

    @Autowired
    public EsUserCommandHandlerImpl(EsUserRepository esUserRepository,
                                    KfProducer producer,
                                    final ReactiveRedisOperations<String, User> operations,
                                    DbSynchronizer synchronizer) {
        this.esUserRepository = esUserRepository;
        this.producer = producer;
        this.cache = operations.opsForHash();
        this.synchronizer = synchronizer;
    }

    @Override
    public Mono<EsUser> apply(CreateUserCommand command) {
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
                    cache.put(RedisConfig.CACHE_KEY, createdEvent.getEntityId(), command.getUser())
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe();
                    synchronizer.sync(createdEvent);
                });
    }

    @Override
    public Mono<EsUser> apply(DeleteUserCommand command) {
        EsUser event = EsUser.builder()
                .type(EsType.USER_DELETED)
                .time(LocalDateTime.now())
                .createdBy(command.getCommandBy())
                .entityId(command.getId())
                .status(EsStatus.PENDING)
                .build();
        return esUserRepository.save(event)
                .doOnNext(createdEvent -> producer.send(EsType.USER_DELETED.toString(), createdEvent));
    }

    @Override
    public void apply(CompleteTransactionCommand command) {
        esUserRepository.findByEntityIdTypeStatus(
                        command.getUserId(),
                        EsType.USER_DELETED
                )
                .collectList()
                .map(resultList -> {
                    if (resultList.isEmpty()) {
                        esUserRepository.findByEntityIdTypeStatus(
                                        command.getUserId(),
                                        EsType.USER_DELETED,
                                        EsStatus.PENDING
                                )
                                .map(esUser -> {
                                    EsUser completeEvent = EsUser.builder()
                                            .type(EsType.USER_DELETED)
                                            .time(LocalDateTime.now())
                                            .createdBy(esUser.getCreatedBy())
                                            .entityId(command.getUserId())
                                            .status(command.getStatus())
                                            .build();
                                    esUserRepository.save(completeEvent)
                                            .subscribeOn(Schedulers.boundedElastic())
                                            .subscribe();
                                    if (EsStatus.SUBMITTED.equals(command.getStatus())) {
                                        cache.remove(RedisConfig.CACHE_KEY, esUser.getEntityId())
                                                .subscribeOn(Schedulers.boundedElastic())
                                                .subscribe();
                                        synchronizer.sync(completeEvent);
                                    }
                                    producer.send(EsType.USER_DELETED.toString(), completeEvent);
                                    return esUser;
                                })
                                .subscribeOn(Schedulers.boundedElastic())
                                .subscribe();
                    }
                    return resultList;
                })
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe();
    }

}
