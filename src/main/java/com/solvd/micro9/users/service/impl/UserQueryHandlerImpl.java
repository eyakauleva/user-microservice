package com.solvd.micro9.users.service.impl;

import com.solvd.micro9.users.domain.aggregate.Gender;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
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
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.CriteriaQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserQueryHandlerImpl implements UserQueryHandler {

    private final UserRepository userRepository;
    private final ReactiveHashOperations<String, String, User> cache;
    private final ReactiveElasticsearchOperations elasticOperations;
    private boolean areAllUsersInCache = false;

    public UserQueryHandlerImpl(final UserRepository userRepository,
                                final ReactiveRedisOperations<String, User> redisOperations,
                                final ReactiveElasticsearchOperations elasticOperations) {
        this.userRepository = userRepository;
        this.cache = redisOperations.opsForHash();
        this.elasticOperations = elasticOperations;
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
    public Flux<ElstcUser> findByCriteria(UserCriteria criteria, Pageable pageable) {
        Criteria elstcCriteria = prepareCriteria(criteria);
        Query query = new CriteriaQuery(elstcCriteria).setPageable(pageable);
        return elasticOperations.search(query, ElstcUser.class)
                .map(SearchHit::getContent); //TODO get from mongo
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
        Criteria searchCriteria = new Criteria();
        if (criteriaData.getName() != null) {
            searchCriteria.and(Criteria.where("full_name")
                    .contains(criteriaData.getName()));
        }
        if (criteriaData.getPhone() != null) {
            searchCriteria.and(Criteria.where("phone")
                    .is(criteriaData.getPhone()));
        }
        if (criteriaData.getAge() != null) {
            searchCriteria.and(Criteria.where("age")
                    .is(criteriaData.getAge()));
        }
        if (criteriaData.getHeightFrom() != null) {
            searchCriteria.and(Criteria.where("height")
                    .greaterThanEqual(criteriaData.getHeightFrom()));
        }
        if (criteriaData.getHeightTo() != null) {
            searchCriteria.and(Criteria.where("height")
                    .lessThanEqual(criteriaData.getHeightTo()));
        }
        if (criteriaData.getWeightFrom() != null) {
            searchCriteria.and(Criteria.where("weight")
                    .greaterThanEqual(criteriaData.getWeightFrom()));
        }
        if (criteriaData.getWeightTo() != null) {
            searchCriteria.and(Criteria.where("weight")
                    .lessThanEqual(criteriaData.getWeightTo()));
        }
        if (criteriaData.getGenders() != null
                && criteriaData.getGenders().size() > 0) {
            searchCriteria.and(Criteria.where("gender")
                    .in(criteriaData.getGenders()));
        }
        if (criteriaData.getEyesColors() != null
                && criteriaData.getEyesColors().size() > 0) {
            searchCriteria.and(Criteria.where("eyesColor")
                    .in(criteriaData.getEyesColors()));
        }
        if (criteriaData.getStudyYear() != null) {
            searchCriteria.and(Criteria.where("studyYears")
                    .is(criteriaData.getStudyYear()));
        }
        return searchCriteria;
    }

    @PreDestroy
    private void cleanCache() {
        cache.delete(RedisConfig.CACHE_KEY).block();
    }

}
