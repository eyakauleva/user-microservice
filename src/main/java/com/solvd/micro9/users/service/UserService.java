package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.Es;
import reactor.core.publisher.Mono;

public interface UserService {

    Mono<User> create(Es eventStore);

    Mono<Void> delete(Es eventStore);

}
