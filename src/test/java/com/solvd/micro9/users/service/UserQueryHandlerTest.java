package com.solvd.micro9.users.service;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ESearchUser;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.persistence.elastic.ElasticFilter;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.cache.RedisConfig;
import com.solvd.micro9.users.service.impl.UserQueryHandlerImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.AbstractMap;
import java.util.Map;

@ExtendWith(MockitoExtension.class)
class UserQueryHandlerTest {

    private UserRepository userRepository;
    private ReactiveHashOperations<String, String, User> cache;
    private ElasticFilter elasticFilter;
    private UserQueryHandlerImpl queryHandler;

    @BeforeEach
    void init() {
        this.userRepository = Mockito.mock(UserRepository.class);
        ReactiveRedisOperations<String, User> rdsOperations =
                Mockito.mock(ReactiveRedisOperations.class);
        this.cache = Mockito.mock(ReactiveHashOperations.class);
        Mockito.doReturn(cache).when(rdsOperations).opsForHash();
        this.elasticFilter = Mockito.mock(ElasticFilter.class);
        this.queryHandler = new UserQueryHandlerImpl(
                userRepository, rdsOperations, elasticFilter
        );
    }

    @Test
    void verifyAllExistingUsersAreFoundInDbTest() {
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
    void verifyAllExistingUsersAreFoundInCacheTest() {
        User user = TestUtils.getUser();
        Map.Entry<String, User> allUsersEntry = new AbstractMap.SimpleEntry<>(
                "test", user
        );
        Mockito.when(cache.entries(RedisConfig.CACHE_KEY))
                .thenReturn(Flux.just(allUsersEntry));
        Mockito.when(userRepository.findAll()).thenReturn(Flux.empty());
        Flux<User> userFluxFromCache = queryHandler.getAll()
                .collectList()
                .flatMapMany(usersFromDb -> queryHandler.getAll());
        StepVerifier.create(userFluxFromCache)
                .expectNext(user)
                .verifyComplete();
    }

    @Test
    void verifyUsersAreFoundByCriteria() {
        UserCriteria criteria = TestUtils.getUserCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        ESearchUser elstcUser = TestUtils.getElstcUser();
        Mockito.when(elasticFilter.doFilter(criteria, pageable))
                .thenReturn(Flux.just(elstcUser));
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, elstcUser.getId()))
                .thenReturn(Mono.just(TestUtils.convertToUser(elstcUser)));
        Flux<User> userFlux = queryHandler.findByCriteria(criteria, pageable);
        StepVerifier.create(userFlux)
                .expectNextCount(1)
                .verifyComplete();
        StepVerifier.create(userFlux)
                .thenConsumeWhile(user -> {
                    Assertions.assertEquals(elstcUser.getId(), user.getId());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void verifyUserIsFoundByIdFromDbTest() {
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
    void verifyUserIsFoundByIdFromCacheTest() {
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
    void verifyUserIsNotFoundByIdTest() {
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
