package com.solvd.micro9.users.service.impl;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.UserQueryHandler;
import com.solvd.micro9.users.service.cache.RedisConfig;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
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
    private boolean areAllUsersInCache = false;

    public UserQueryHandlerImpl(final UserRepository userRepository,
                                final ReactiveRedisOperations<String, User> operations) {
        this.userRepository = userRepository;
        this.cache = operations.opsForHash();
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
    public Flux<User> findByCriteria(UserCriteria criteria) {
        Criteria elstcCriteria = prepareCriteria(criteria);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");
        Pageable pageable = PageRequest.of(criteria.getPage(), criteria.getSize());
        CriteriaQuery query = new CriteriaQuery(elstcCriteria, pageable).addSort(sort);
        //List<YourEntity> result = elasticsearchTemplate.queryForList(query, YourEntity.class);

        //ElasticsearchTemplate

        return Flux.empty();
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

    private Criteria prepareCriteria(UserCriteria criteriaData) {
        if (criteriaData.getPage() == null) {
            criteriaData.setPage(0);
        }
        if (criteriaData.getSize() == null) {
            criteriaData.setSize(10);
        }
        Criteria searchCriteria = new Criteria();
        if (criteriaData.getName() != null) {
            searchCriteria.and("fullName")
                    .contains(criteriaData.getName());
        }
        if (criteriaData.getPhone() != null) {
            searchCriteria.and("phone")
                    .is(criteriaData.getPhone());
        }
        if (criteriaData.getAge() != null) {
            searchCriteria.and("age")
                    .is(criteriaData.getAge());
        }
        if (criteriaData.getHeightFrom() != null) {
            searchCriteria.and("height")
                    .greaterThanEqual(criteriaData.getHeightFrom());
        }
        if (criteriaData.getHeightTo() != null) {
            searchCriteria.and("height")
                    .lessThanEqual(criteriaData.getHeightTo());
        }
        if (criteriaData.getWeightFrom() != null) {
            searchCriteria.and("weight")
                    .greaterThanEqual(criteriaData.getWeightFrom());
        }
        if (criteriaData.getWeightTo() != null) {
            searchCriteria.and("weight")
                    .lessThanEqual(criteriaData.getWeightTo());
        }
        if (criteriaData.getGenders().size() != 0) {
            searchCriteria.and("gender")
                    .in(criteriaData.getGenders());
        }
        if (criteriaData.getEyesColors().size() != 0) {
            searchCriteria.and("eyesColor")
                    .in(criteriaData.getEyesColors());
        }
        if (criteriaData.getStudyYear() != null) {
            searchCriteria.and("studyYears")
                    .is(criteriaData.getStudyYear());
        }
        return searchCriteria;
    }

    @PreDestroy
    private void cleanCache() {
        cache.delete(RedisConfig.CACHE_KEY).block();
    }

}
