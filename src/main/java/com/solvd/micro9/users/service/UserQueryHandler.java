package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserQueryHandler {

    Flux<User> getAll();

    Flux<ElstcUser> findByCriteria(UserCriteria criteria, Pageable pageable);

    Mono<User> findById(EsUserQuery query);

}
