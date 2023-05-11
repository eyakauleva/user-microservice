package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.cache.RedisConfig;
import com.solvd.micro9.users.service.impl.UserQueryHandlerImpl;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    public void init() {
        this.userRepository = Mockito.mock(UserRepository.class);
        ReactiveRedisOperations<String, User> operations = Mockito.mock(ReactiveRedisOperations.class);
        this.cache = Mockito.mock(ReactiveHashOperations.class);
        Mockito.doReturn(cache).when(operations).opsForHash();
        this.queryHandler = new UserQueryHandlerImpl(userRepository, operations);
    }

    @Test
    public void verifyAllExistingUsersAreFoundTest() {
        //given
        User user = new User("1111", "Liza", "Ya", "email@gmail.com", false);

        Flux<User> allUsers = Flux.just(user);
        Mockito.when(userRepository.findAll()).thenReturn(allUsers);
        Mockito.when(cache.put(RedisConfig.CACHE_KEY, user.getId(), user)).thenReturn(Mono.just(true));
        Map.Entry<String, User> allUsersEntry = new AbstractMap.SimpleEntry<>("test", user);
        Mockito.when(cache.entries(RedisConfig.CACHE_KEY)).thenReturn(Flux.just(allUsersEntry));

        //when
        Flux<User> userFluxFromDb = queryHandler.getAll();
        Flux<User> userFluxFromCache = queryHandler.getAll();

        //then
        StepVerifier.create(userFluxFromDb)
                .expectNext(user)
                .verifyComplete();

        StepVerifier.create(userFluxFromCache)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void verifyUserIsFoundByIdFromDbTest() {
        //given
        String userId = "1111";
        User user = new User(userId, "Liza", "Ya", "email@gmail.com", false);
        EsUserQuery query = new EsUserQuery(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Mono.just(user));
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, query.getId())).thenReturn(Mono.empty());
        Mockito.when(cache.put(RedisConfig.CACHE_KEY, user.getId(), user)).thenReturn(Mono.just(true));

        //when
        Mono<User> foundUser = queryHandler.findById(query);

        //then
        StepVerifier.create(foundUser)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void verifyUserIsFoundByIdFromCacheTest() {
        //given
        String userId = "1111";
        User user = new User(userId, "Liza", "Ya", "email@gmail.com", false);
        EsUserQuery query = new EsUserQuery(userId);

        Mockito.when(cache.get(RedisConfig.CACHE_KEY, query.getId())).thenReturn(Mono.just(user));

        //when
        Mono<User> foundUser = queryHandler.findById(query);

        //then
        StepVerifier.create(foundUser)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    public void verifyUserIsNotFoundByIdTest() {
        //given
        String userId = "1111";
        EsUserQuery query = new EsUserQuery(userId);

        Mockito.when(userRepository.findById(userId)).thenReturn(Mono.empty());
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, query.getId())).thenReturn(Mono.empty());

        //when
        Mono<User> foundUser = queryHandler.findById(query);

        //then
        StepVerifier.create(foundUser)
                .expectErrorMatches(throwable -> throwable instanceof ResourceDoesNotExistException)
                .verify();
    }

}
