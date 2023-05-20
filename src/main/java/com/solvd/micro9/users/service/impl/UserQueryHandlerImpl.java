package com.solvd.micro9.users.service.impl;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.persistence.elastic.ReactiveElasticFilter;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.UserQueryHandler;
import com.solvd.micro9.users.service.cache.RedisConfig;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
@Slf4j
public class UserQueryHandlerImpl implements UserQueryHandler {

    private final UserRepository userRepository;
    private final ReactiveHashOperations<String, String, User> cache;
    private final ReactiveElasticFilter elasticFilter;
    private boolean areAllUsersInCache = false;

    public UserQueryHandlerImpl(final UserRepository userRepository,
                                final ReactiveRedisOperations<String, User> rdsOperations,
                                final ReactiveElasticFilter elasticFilter) {
        this.userRepository = userRepository;
        this.cache = rdsOperations.opsForHash();
        this.elasticFilter = elasticFilter;
    }

    @Override
    public Flux<User> getAll() {
        if (areAllUsersInCache) {
            return cache.entries(RedisConfig.CACHE_KEY)
                    .map(Map.Entry::getValue);
        } else {
            areAllUsersInCache = true;
            return userRepository.findAll()
                    .doOnNext(user -> cache.put(RedisConfig.CACHE_KEY, user.getId(), user)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe());
        }
    }

    @Override
    public Flux<User> findByCriteria(final UserCriteria criteria,
                                     final Pageable pageable) {
        return elasticFilter.doFilter(criteria, pageable)
                .flatMap(elstcUser -> this.findById(
                        new EsUserQuery(elstcUser.getId()))
                );
    }

    @Override
    public Mono<User> findById(final EsUserQuery query) {
        return cache.get(RedisConfig.CACHE_KEY, query.getId())
                .switchIfEmpty(Mono.defer(() -> fromDbToCache(query.getId())));
    }

    private Mono<User> fromDbToCache(final String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono
                        .error(
                                new ResourceDoesNotExistException(
                                        "User [id=" + id + "] does not exist"
                                )
                        )
                )
                .flatMap(user -> cache.put(RedisConfig.CACHE_KEY, id, user)
                        .thenReturn(user));
    }

    @PreDestroy
    private void cleanCache() {
        cache.delete(RedisConfig.CACHE_KEY).block();
    }

}
