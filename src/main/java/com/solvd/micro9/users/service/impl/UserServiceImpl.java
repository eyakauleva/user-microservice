package com.solvd.micro9.users.service.impl;

import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.messaging.KfProducer;
import com.solvd.micro9.users.persistence.UserRepository;
import com.solvd.micro9.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KfProducer producer;

    public Flux<User> getAll() {
        return userRepository.findAll();
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new ResourceDoesNotExistException("User [id=" + id + "] does not exist")));
    }

    public Mono<Void> delete(Long id) {
        return userRepository.deleteById(id)
                .doOnSuccess(i -> producer.send("Deleted user's id", id));
    }

}
