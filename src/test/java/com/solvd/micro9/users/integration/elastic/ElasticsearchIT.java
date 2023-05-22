package com.solvd.micro9.users.integration.elastic;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.UserQueryHandler;
import com.solvd.micro9.users.service.impl.UserQueryHandlerImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

@Slf4j
@SpringBootTest(classes = {ElasticITContext.class, UserQueryHandlerImpl.class})
@DirtiesContext
class ElasticsearchIT extends ElasticTestcontainers {

    @Autowired
    private ElstcUserRepository elstcRepository;

    @Autowired
    private UserQueryHandler queryHandler;

    @MockBean
    private UserRepository userRepository;

    @Test
    @SneakyThrows
    void verifyElasticsearchUsersAreFoundByCriteria() {
        UserCriteria criteria = TestUtils.getUserCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        List<ElstcUser> elstcUsers = TestUtils.getElstcUsers();
        elstcUsers.forEach(elstcUser ->
                Mockito.when(userRepository.findById(elstcUser.getId()))
                .thenReturn(Mono.just(TestUtils.convertToUser(elstcUser))));
        long appropriateUsersCount = 1;
        elstcRepository.saveAll(elstcUsers);
        Flux<User> userFlux = queryHandler.findByCriteria(criteria, pageable);
        StepVerifier.create(userFlux)
                .expectNextCount(appropriateUsersCount)
                .verifyComplete();
        StepVerifier.create(userFlux)
                .thenConsumeWhile(user -> {
                    boolean doesUserContainCriteriaName =
                            user.getFirstName().contains(criteria.getName())
                                    || user.getLastName().contains(criteria.getName());
                    Assertions.assertTrue(doesUserContainCriteriaName);
                    Assertions.assertEquals(criteria.getAge(), user.getAge());
                    Assertions.assertTrue(
                            user.getHeight() >= criteria.getHeightFrom()
                                    && user.getHeight() <= criteria.getHeightTo()
                    );
                    Assertions.assertTrue(
                            user.getWeight() >= criteria.getWeightFrom()
                                    && user.getWeight() <= criteria.getWeightTo()
                    );
                    Assertions.assertTrue(
                            criteria.getGenders().contains(user.getGender())
                    );
                    Assertions.assertTrue(
                            criteria.getEyesColors().contains(user.getEyesColor())
                    );
                    Assertions.assertTrue(
                            criteria.getStudyYear() >= user.getStartStudyYear()
                                    && criteria.getStudyYear() <= user.getEndStudyYear()
                    );
                    return true;
                })
                .verifyComplete();
    }

}
