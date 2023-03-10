package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.User;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {

    Flux<User> getAll();

    Mono<User> findById(Long id);

}
