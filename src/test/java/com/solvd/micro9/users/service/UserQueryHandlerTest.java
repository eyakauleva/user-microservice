package com.solvd.micro9.users.service;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import com.solvd.micro9.users.domain.elasticsearch.StudyYears;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.mongodb.core.query.Criteria;
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

    private ReactiveElasticsearchOperations elasticOperations;

    private UserQueryHandlerImpl queryHandler;

    @BeforeAll
    public void init() {
        this.userRepository = Mockito.mock(UserRepository.class);
        ReactiveRedisOperations<String, User> redisOperations =
                Mockito.mock(ReactiveRedisOperations.class);
        this.cache = Mockito.mock(ReactiveHashOperations.class);
        Mockito.doReturn(cache).when(redisOperations).opsForHash();
        this.elasticOperations = Mockito.mock(ReactiveElasticsearchTemplate.class);
        this.queryHandler = new UserQueryHandlerImpl(
                userRepository, redisOperations, elasticOperations
        );
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
    void verifyUsersAreFoundByCriteria() { //TODO
        UserCriteria criteria = TestUtils.getUserCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        ElstcUser elstcUser1 = new ElstcUser("1", "Liza Ya", "+12345",
                20, Gender.FEMALE, 170.5f, 50.2f, EyesColor.BLUE,
                new StudyYears(2015, 2018));
        ElstcUser elstcUser2 = new ElstcUser("2", "Ivan Ivanov", "+898765",
                32, Gender.MALE, 185.3f, 94f, EyesColor.GREEN,
                new StudyYears(2010, 2014));
        ElstcUser elstcUser3 = new ElstcUser("3", "Sasha La", "+9438403",
                16, Gender.FEMALE, 155.67f, 49.8f, EyesColor.BROWN,
                new StudyYears(2025, 2030));
        Flux<SearchHit<ElstcUser>> searchHitFlux = Flux.just(
                new SearchHit<>(null, null, null, 0f, null, null, null, null, null, null,
                        elstcUser1),
                new SearchHit<>(null, null, null, 0f, null, null, null, null, null, null,
                        elstcUser2),
                new SearchHit<>(null, null, null, 0f, null, null, null, null, null, null,
                        elstcUser3)
        );
        User user1 = new User("1", "Liza", "Ya", "liza@email", "+12345", 20, Gender.FEMALE,
                170.5f, 50.2f, EyesColor.BLUE, 2015, 2018, false);
        User user2 = new User("2", "Ivan", "Ivanov", "ivan@email", "+898765",
                32, Gender.MALE, 185.3f, 94f, EyesColor.GREEN, 2010, 2014, false);
        User user3 = new User("3", "Sasha", "La", "sasha@email", "+9438403",
                16, Gender.FEMALE, 155.67f, 49.8f, EyesColor.BROWN, 2025, 2030, false);
        Mockito.when(
                elasticOperations.search(Mockito.any(CriteriaQuery.class),
                        ElstcUser.class)
        ).thenReturn(searchHitFlux);
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, elstcUser1.getId()))
                .thenReturn(Mono.just(user1));
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, elstcUser2.getId()))
                .thenReturn(Mono.just(user2));
        Mockito.when(cache.get(RedisConfig.CACHE_KEY, elstcUser3.getId()))
                .thenReturn(Mono.just(user3));
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
