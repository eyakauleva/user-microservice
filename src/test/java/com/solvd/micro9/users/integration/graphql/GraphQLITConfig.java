package com.solvd.micro9.users.integration.graphql;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.service.UserQueryHandler;
import com.solvd.micro9.users.service.impl.UserQueryHandlerImpl;
import com.solvd.micro9.users.web.WebConfig;
import com.solvd.micro9.users.web.controller.graphql.LocalDateTimeToStringCoercing;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Flux;

@TestConfiguration
@EnableAutoConfiguration
@Import({WebConfig.class, LocalDateTimeToStringCoercing.class})
public class GraphQLITConfig {

    @Bean
    public UserQueryHandler queryHandler() {
        UserQueryHandlerImpl queryHandler = Mockito.mock(UserQueryHandlerImpl.class);
        Mockito.when(queryHandler.getAll()).thenReturn(Flux.just(TestUtils.getUser()));
        return queryHandler;
    }

}
