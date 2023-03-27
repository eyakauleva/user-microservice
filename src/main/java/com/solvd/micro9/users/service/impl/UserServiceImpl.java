package com.solvd.micro9.users.service.impl;

import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.messaging.KfProducer;
import com.solvd.micro9.users.persistence.UserRepository;
import com.solvd.micro9.users.service.UserService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KfProducer producer;
    private final ReactiveHashOperations<String, Long, User> cache;
    private final String cacheKey = "users";
    private boolean areAllUsersInCache = false;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           KfProducer producer,
                           final ReactiveRedisOperations<String, User> operations) {
        this.userRepository = userRepository;
        this.producer = producer;
        this.cache = operations.opsForHash();
    }

    public Flux<User> getAll() {
        if (areAllUsersInCache) {
            return cache.entries(cacheKey)
                    .map(Map.Entry::getValue);
        } else {
            areAllUsersInCache = true;
            return userRepository.findAll()
                    .doOnNext(user -> cache.put(cacheKey, user.getId(), user)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe());
        }
    }

    public Mono<User> findById(Long id) {
        cache.remove(cacheKey, 8L);
        return cache.get(cacheKey, id)
                .switchIfEmpty(Mono.defer(() -> fromDbToCache(id)));
    }

    public Mono<Void> delete(Long id) {
        return cache.remove(cacheKey, id)
                .flatMap(returnedId -> userRepository.deleteById(id))
                .doOnSuccess(i -> {
                    producer.send("Deleted user's id", id);
                    cache.remove(cacheKey, id);
                });
    }

    private Mono<User> fromDbToCache(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceDoesNotExistException("User [id=" + id + "] does not exist")))
                .flatMap(user -> cache.put(cacheKey, id, user)
                        .thenReturn(user));
    }

    @PreDestroy
    private void cleanCache() {
        cache.delete(cacheKey).block();
    }

}
