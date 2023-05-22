package com.solvd.micro9.users.service.impl;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import com.solvd.micro9.users.integration.elastic.ElasticTestcontainers;
import com.solvd.micro9.users.integration.elastic.ElstcConfig;
import com.solvd.micro9.users.integration.elastic.ElstcUserRepository;
import com.solvd.micro9.users.persistence.snapshot.UserRepository;
import com.solvd.micro9.users.service.UserQueryHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Set;

@Slf4j
@SpringBootTest(classes = ElstcConfig.class)
@DirtiesContext
class ElasticsearchIT extends ElasticTestcontainers {

    @Autowired
    private ElstcUserRepository elstcRepository;

    @Autowired
    private UserQueryHandler queryHandler;

    @MockBean
    private UserRepository userRepository;


    //TODO exclude kafka, & mock mongo and redis

    @Test
    @SneakyThrows
    void verifyElasticsearchUsersAreFoundByCriteria() {
        UserCriteria criteria = TestUtils.getUserCriteria();
        Pageable pageable = PageRequest.of(0, 10);
        List<ElstcUser> elstcUsers = TestUtils.getElstcUsers();
        elstcUsers.forEach(elstcUser -> Mockito.when(userRepository.findById(elstcUser.getId()))
                .thenReturn(Mono.just(TestUtils.convertToUser(elstcUser))));
        elstcRepository.saveAll(elstcUsers);
        Flux<User> userFlux = queryHandler.findByCriteria(criteria, pageable);

        StepVerifier.create(userFlux)
                        .expectNextCount(1)
                                .verifyComplete(); //TODO remove

//        StepVerifier.create(userFlux)
//                .thenConsumeWhile(user -> {
//                    boolean doesUserContainCriteriaName =
//                            user.getFirstName().contains(criteria.getName())
//                                    || user.getLastName().contains(criteria.getName());
//                    Assertions.assertTrue(doesUserContainCriteriaName);
//                    Assertions.assertEquals(criteria.getAge(), user.getAge());
//                    Assertions.assertTrue(
//                            user.getHeight() >= criteria.getHeightFrom()
//                                    && user.getHeight() <= criteria.getHeightTo()
//                    );
//                    Assertions.assertTrue(
//                            user.getWeight() >= criteria.getWeightFrom()
//                                    && user.getWeight() <= criteria.getWeightTo()
//                    );
//                    Assertions.assertTrue(criteria.getGenders().contains(user.getGender()));
//                    Assertions.assertTrue(criteria.getEyesColors().contains(user.getEyesColor()));
//                    Assertions.assertTrue(
//                            criteria.getStudyYear() >= user.getStartStudyYear()
//                                    && criteria.getStudyYear() <= user.getEndStudyYear()
//                    );
//                    return true;
//                })
//                .verifyComplete();
    }

}
