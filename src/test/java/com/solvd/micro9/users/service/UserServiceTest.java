package com.solvd.micro9.users.service;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
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
        String userId = "1111";
        User user = new User(userId, "Liza", "Ya", "email@gmail.com",
                "+12345", 20, Gender.FEMALE, 170.5f,
                50.2f, EyesColor.BLUE, true);
        User savedUser = new User(userId, "Liza", "Ya", "email@gmail.com",
                "+12345", 20, Gender.FEMALE, 170.5f,
                50.2f, EyesColor.BLUE, false);
        String payload = new Gson().toJson(user);
        Es eventStore = new Es(1L, EsType.USER_CREATED, LocalDateTime.now(),
                "Liza", userId, payload, EsStatus.SUBMITTED
        );
        Mockito.when(userRepository.save(user)).thenReturn(Mono.just(savedUser));
        Mono<User> createdUser = userService.create(eventStore);
        StepVerifier.create(createdUser)
                .expectNext(savedUser)
                .verifyComplete();
        Mockito.verify(producer, Mockito.times(1)).send(userId, savedUser);
    }

    @Test
    public void verifyUserIsDeleted() {
        String userId = "1111";
        Es eventStore = new Es(1L, EsType.USER_DELETED, LocalDateTime.now(),
                "Liza", userId, null, EsStatus.SUBMITTED
        );
        Mockito.when(userRepository.deleteById(userId)).thenReturn(Mono.empty());
        Mono<Void> result = userService.delete(eventStore);
        StepVerifier.create(result)
                .verifyComplete();

        Mockito.verify(producer, Mockito.times(1)).send(userId, null);
    }

}
