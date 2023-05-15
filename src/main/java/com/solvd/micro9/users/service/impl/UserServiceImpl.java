package com.solvd.micro9.users.service.impl;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import com.solvd.micro9.users.domain.elasticsearch.StudyYears;
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
    private final KfProducer<String, ElstcUser> producer;

    @Override
    public Mono<User> create(final Es eventStore) {
        User user = new Gson().fromJson(eventStore.getPayload(), User.class);
        user.setId(eventStore.getEntityId());
        user.setNew(true);
        return userRepository.save(user)
                .doOnNext(savedUser -> {
                    ElstcUser elstcUser = new ElstcUser(
                            savedUser.getId(),
                            savedUser.getFirstName() + " " + savedUser.getLastName(),
                            savedUser.getPhone(),
                            savedUser.getAge(),
                            savedUser.getGender(),
                            savedUser.getHeight(),
                            savedUser.getWeight(),
                            savedUser.getEyesColor(),
                            new StudyYears(
                                    savedUser.getStartStudyYear(),
                                    savedUser.getEndStudyYear()
                            ));
                    producer.send(savedUser.getId(), elstcUser);
                });
    }

    @Override
    public Mono<Void> delete(final Es eventStore) {
        return userRepository.deleteById(eventStore.getEntityId())
                .doOnSuccess(voidReturned ->
                        producer.send(eventStore.getEntityId(), null));
    }

}
