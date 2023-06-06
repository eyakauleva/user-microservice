package com.solvd.micro9.users.integration.elastic;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ESearchUser;
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
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest(classes = {ElasticsearchITConfig.class, UserQueryHandlerImpl.class})
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
        List<ESearchUser> eSearchUsers = TestUtils.getElstcUsers();
        List<String> ids = new ArrayList<>();
        List<Flux<User>> usersFluxList = new ArrayList<>(List.of(Flux.empty()));
        List<Long> appropriateUsersCountList = new ArrayList<>(List.of(0L));
        eSearchUsers.stream()
                .filter(eSearchUser ->
                        TestUtils.doesESearchUserSatisfyCriteria(eSearchUser, criteria))
                .forEach(eSearchUser -> {
                    ids.add(eSearchUser.getId());
                    usersFluxList.set(
                            0,
                            Flux.concat(
                                    usersFluxList.get(0),
                                    Flux.just(TestUtils.convertToUser(eSearchUser))
                            )
                    );
                    appropriateUsersCountList.set(0, appropriateUsersCountList.get(0) + 1);
                });
        Mockito.when(userRepository.findAllById(ids)).thenReturn(usersFluxList.get(0));
        elstcRepository.saveAll(eSearchUsers);
        Flux<User> userFlux = queryHandler.findByCriteria(criteria, pageable);
        StepVerifier.create(userFlux)
                .expectNextCount(appropriateUsersCountList.get(0))
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
