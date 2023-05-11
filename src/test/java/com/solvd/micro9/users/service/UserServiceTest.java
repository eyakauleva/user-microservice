package com.solvd.micro9.users.service;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.messaging.KfProducer;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KfProducer<String, User> producer;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    public void verifyUserIsCreatedTest() {
        //given
        String userId = "1111";
        User user = new User(userId, "Liza", "Ya", "email@gmail.com", true);
        User savedUser = new User(userId, "Liza", "Ya", "email@gmail.com", false);
        String payload = new Gson().toJson(user);
        Es eventStore = new Es(1L, EsType.USER_CREATED, LocalDateTime.now(), "Liza", userId, payload, EsStatus.SUBMITTED);

        Mockito.when(userRepository.save(user)).thenReturn(Mono.just(savedUser));

        //when
        Mono<User> createdUser = userService.create(eventStore);

        //then
        StepVerifier.create(createdUser)
                .expectNext(savedUser)
                .verifyComplete();

        verify(producer, only()).send(userId, savedUser);
    }

    @Test
    public void verifyUserIsDeleted() {
        //given
        String userId = "1111";
        Es eventStore = new Es(1L, EsType.USER_DELETED, LocalDateTime.now(), "Liza", userId, null, EsStatus.SUBMITTED);

        Mockito.when(userRepository.deleteById(userId)).thenReturn(Mono.empty());

        //when
        Mono<Void> result = userService.delete(eventStore);

        //then
        StepVerifier.create(result)
                .verifyComplete();

        verify(producer, only()).send(userId, null);
    }

}
