package com.solvd.micro9.users.service;

import com.google.gson.Gson;
import com.solvd.micro9.users.TestUtils;
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
import com.solvd.micro9.users.service.cache.RedisConfig;
import com.solvd.micro9.users.service.impl.EsUserCommandHandlerImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@ExtendWith(MockitoExtension.class)
class EsUserCommandHandlerTest {

    private EsUserRepository esUserRepository;
    private KfProducer<String, Es> producer;
    private ReactiveHashOperations<String, String, User> cache;
    private Map<EsType, Function<Es, Mono<?>>> dbSynchronizerHandler;
    private EsUserCommandHandlerImpl commandHandler;

    @BeforeEach
    void init() {
        this.esUserRepository = Mockito.mock(EsUserRepository.class);
        this.producer = Mockito.mock(KfProducer.class);
        ReactiveRedisOperations<String, User> operations =
                Mockito.mock(ReactiveRedisOperations.class);
        this.cache = Mockito.mock(ReactiveHashOperations.class);
        Mockito.doReturn(cache).when(operations).opsForHash();
        this.dbSynchronizerHandler = Mockito.mock(Map.class);
        this.commandHandler = new EsUserCommandHandlerImpl(
                esUserRepository, producer, operations, dbSynchronizerHandler
        );
    }

    @Test
    void verifyCreateUserCommandIsApplied() {
        User user = TestUtils.getUser();
        CreateUserCommand command = new CreateUserCommand(user, "Liza");
        String payload = new Gson().toJson(command.getUser());
        EsUser createdEvent = EsUser.builder()
                .id(999L)
                .type(EsType.USER_CREATED)
                .time(LocalDateTime.now())
                .createdBy(command.getCommandBy())
                .entityId(UUID.randomUUID().toString())
                .payload(payload)
                .status(EsStatus.SUBMITTED)
                .build();
        Mockito.when(esUserRepository.save(Mockito.any(EsUser.class)))
                .thenReturn(Mono.just(createdEvent));
        Mockito.when(cache.put(
                        RedisConfig.CACHE_KEY,
                        createdEvent.getEntityId(),
                        command.getUser())
                )
                .thenReturn(Mono.just(true));
        Mockito.when(dbSynchronizerHandler.get(createdEvent.getType()))
                .thenReturn((es) -> Mono.empty());
        Mono<EsUser> result = commandHandler.apply(command);
        StepVerifier.create(result)
                .expectNext(createdEvent)
                .verifyComplete();
        Mockito.verify(cache, Mockito.times(1)).put(
                RedisConfig.CACHE_KEY, createdEvent.getEntityId(), command.getUser()
        );
        Mockito.verify(dbSynchronizerHandler, Mockito.times(1))
                .get(createdEvent.getType());
    }

    @Test
    void verifyDeleteUserCommandIsApplied() {
        DeleteUserCommand command = new DeleteUserCommand("111", "Liza");
        EsUser createdEvent = EsUser.builder()
                .id(999L)
                .type(EsType.USER_DELETED)
                .time(LocalDateTime.now())
                .createdBy(command.getCommandBy())
                .entityId(command.getId())
                .status(EsStatus.PENDING)
                .build();
        Mockito.when(esUserRepository.save(Mockito.any(EsUser.class)))
                .thenReturn(Mono.just(createdEvent));
        Mono<EsUser> result = commandHandler.apply(command);
        StepVerifier.create(result)
                .expectNext(createdEvent)
                .verifyComplete();
        Mockito.verify(producer, Mockito.times(1))
                .send(EsType.USER_DELETED.toString(), createdEvent);
    }

    @Test
    void verifyCompleteTransactionCommandIsApplied() {
        CompleteTransactionCommand command =
                new CompleteTransactionCommand("12345", EsStatus.SUBMITTED);
        EsUser event = EsUser.builder()
                .id(999L)
                .type(EsType.USER_DELETED)
                .time(LocalDateTime.now())
                .createdBy("Liza")
                .entityId("12345")
                .status(EsStatus.PENDING)
                .build();
        Mockito.when(esUserRepository.findByEntityIdTypeStatus(
                command.getUserId(),
                EsType.USER_DELETED,
                EsStatus.PENDING
        )).thenReturn(Flux.just(event));
        Mockito.when(esUserRepository.save(Mockito.any(EsUser.class)))
                .thenReturn(Mono.just(event));
        Mockito.when(cache.remove(
                        RedisConfig.CACHE_KEY,
                        event.getEntityId()
                )
        ).thenReturn(Mono.just(1L));
        Mockito.when(dbSynchronizerHandler.get(event.getType()))
                .thenReturn((es) -> Mono.empty());
        Flux<EsUser> result = commandHandler.apply(command);
        StepVerifier.create(result)
                .expectNextCount(1)
                .verifyComplete();
        Mockito.verify(esUserRepository, Mockito.times(1)).save(Mockito.any(EsUser.class));
        Mockito.verify(cache, Mockito.times(1)).remove(
                RedisConfig.CACHE_KEY,
                event.getEntityId()
        );
        Mockito.verify(dbSynchronizerHandler, Mockito.times(1)).get(event.getType());
        Mockito.verify(producer, Mockito.times(1))
                .send(Mockito.anyString(), Mockito.any(EsUser.class));
    }

}
