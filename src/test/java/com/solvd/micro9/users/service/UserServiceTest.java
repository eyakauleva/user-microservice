package com.solvd.micro9.users.service;

import com.google.gson.Gson;
import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
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
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private KfProducer<String, ElstcUser> producer;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void verifyUserIsCreatedTest() {
        User user = TestUtils.getUser();
        user.setNew(true);
        User savedUser = TestUtils.getUser();
        String payload = new Gson().toJson(user);
        Es eventStore = new Es(1L, EsType.USER_CREATED, LocalDateTime.now(),
                "Liza", user.getId(), payload, EsStatus.SUBMITTED
        );
        Mockito.when(userRepository.save(user)).thenReturn(Mono.just(savedUser));
        Mono<User> createdUser = userService.create(eventStore);
        StepVerifier.create(createdUser)
                .expectNext(savedUser)
                .verifyComplete();
        Mockito.verify(producer, Mockito.times(1)).send(
                Mockito.anyString(),
                Mockito.any(ElstcUser.class)
        );
    }

    @Test
    void verifyUserIsDeleted() {
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
