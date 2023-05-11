package com.solvd.micro9.users.service.impl;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.messaging.KfProducer;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final KfProducer<String, User> producer;

    @Override
    public Mono<User> create(final Es eventStore) {
        User user = new Gson().fromJson(eventStore.getPayload(), User.class);
        user.setId(eventStore.getEntityId());
        user.setNew(true);
        return userRepository.save(user)
                .doOnNext(savedUser -> producer.send(savedUser.getId(), savedUser));
    }

    @Override
    public Mono<Void> delete(final Es eventStore) {
        return userRepository.deleteById(eventStore.getEntityId())
                .doOnSuccess(voidReturned ->
                        producer.send(eventStore.getEntityId(), null));
    }

}
