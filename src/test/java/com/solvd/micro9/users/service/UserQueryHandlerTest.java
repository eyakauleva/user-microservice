package com.solvd.micro9.users.service;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.cache.RedisConfig;
import com.solvd.micro9.users.service.impl.UserQueryHandlerImpl;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.AbstractMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserQueryHandlerTest {

    private UserRepository userRepository;

    private ReactiveHashOperations<String, String, User> cache;

    private UserQueryHandlerImpl queryHandler;

    @BeforeAll
    public void init() {
        this.userRepository = Mockito.mock(UserRepository.class);
        ReactiveRedisOperations<String, User> operations =
                Mockito.mock(ReactiveRedisOperations.class);
        this.cache = Mockito.mock(ReactiveHashOperations.class);
        Mockito.doReturn(cache).when(operations).opsForHash();
        this.queryHandler = new UserQueryHandlerImpl(userRepository, operations);
    }

    @Test
    @Order(1)
    public void verifyAllExistingUsersAreFoundInDbTest() {
        User user = TestUtils.getUser();
        Flux<User> allUsers = Flux.just(user);
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        Mockito.when(cache.put(RedisConfig.CACHE_KEY, user.getId(), user))
                .thenReturn(Mono.just(true));
        Flux<User> userFluxFromDb = queryHandler.getAll();
        StepVerifier.create(userFluxFromDb)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    @Order(2)
    public void verifyAllExistingUsersAreFoundInCacheTest() {
        User user = TestUtils.getUser();
        Map.Entry<String, User> allUsersEntry = new AbstractMap.SimpleEntry<>(
                "test", user
        );
        Mockito.when(cache.entries(RedisConfig.CACHE_KEY))
                .thenReturn(Flux.just(allUsersEntry));
        Flux<User> userFluxFromCache = queryHandler.getAll();
        StepVerifier.create(userFluxFromCache)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void verifyUserIsFoundByIdFromDbTest() {
        User user = TestUtils.getUser();
        EsUserQuery query = new EsUserQuery(user.getId());
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Mono.just(user));
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, query.getId()))
                .thenReturn(Mono.empty());
        Mockito.when(cache.put(RedisConfig.CACHE_KEY, user.getId(), user))
                .thenReturn(Mono.just(true));
        Mono<User> foundUser = queryHandler.findById(query);
        StepVerifier.create(foundUser)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void verifyUserIsFoundByIdFromCacheTest() {
        User user = TestUtils.getUser();
        EsUserQuery query = new EsUserQuery(user.getId());
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, query.getId()))
                .thenReturn(Mono.just(user));
        Mono<User> foundUser = queryHandler.findById(query);
        StepVerifier.create(foundUser)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void verifyUserIsNotFoundByIdTest() {
        String userId = "1111";
        EsUserQuery query = new EsUserQuery(userId);
        Mockito.when(userRepository.findById(userId)).thenReturn(Mono.empty());
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, query.getId()))
                .thenReturn(Mono.empty());
        Mono<User> foundUser = queryHandler.findById(query);
        StepVerifier.create(foundUser)
                .expectErrorMatches(throwable ->
                        throwable instanceof ResourceDoesNotExistException)
                .verify();
    }

}
