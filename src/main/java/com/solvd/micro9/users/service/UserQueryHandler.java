package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserQueryHandler {

    Flux<User> getAll();

    Flux<User> findByCriteria(UserCriteria criteria);

    Mono<User> findById(EsUserQuery query);

}
