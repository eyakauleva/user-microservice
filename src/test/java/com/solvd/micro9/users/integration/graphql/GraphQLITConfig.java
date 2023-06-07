package com.solvd.micro9.users.integration.graphql;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.command.CreateUserCommand;
import com.solvd.micro9.users.domain.command.DeleteUserCommand;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import com.solvd.micro9.users.service.UserQueryHandler;
import com.solvd.micro9.users.service.impl.EsUserCommandHandlerImpl;
import com.solvd.micro9.users.service.impl.UserQueryHandlerImpl;
import com.solvd.micro9.users.web.WebConfig;
import com.solvd.micro9.users.web.controller.graphql.LocalDateTimeToStringCoercing;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@TestConfiguration
@EnableAutoConfiguration
@Import({WebConfig.class, LocalDateTimeToStringCoercing.class})
public class GraphQLITConfig {

    @Bean
    public UserQueryHandler queryHandler() {
        UserQueryHandlerImpl queryHandler = Mockito.mock(UserQueryHandlerImpl.class);
        Mockito.when(queryHandler.getAll()).thenReturn(Flux.just(TestUtils.getUser()));
        Mockito
                .when(
                        queryHandler.findByCriteria(
                                Mockito.any(UserCriteria.class),
                                Mockito.any(Pageable.class)
                        )
                )
                .thenReturn(Flux.just(TestUtils.getUser()));

        Mockito.when(queryHandler.findById(Mockito.any(EsUserQuery.class)))
                .thenReturn(Mono.error(
                        new ResourceDoesNotExistException("User not found")
                ));
        Mockito.when(queryHandler.findById(new EsUserQuery(TestUtils.getUser().getId())))
                .thenReturn(Mono.just(TestUtils.getUser()));
        return queryHandler;
    }

    @Bean
    public EsUserCommandHandler commandHandler() {
        EsUserCommandHandlerImpl commandHandler =
                Mockito.mock(EsUserCommandHandlerImpl.class);
        Mockito.when(commandHandler.apply(Mockito.any(CreateUserCommand.class)))
                .thenReturn(Mono.just(TestUtils.getEsUserCreated()));
        Mockito.when(commandHandler.apply(Mockito.any(DeleteUserCommand.class)))
                .thenReturn(Mono.just(TestUtils.getEsUserDeleted()));
        return commandHandler;
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer testJsonCustomizer() {
        String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
        return builder -> builder.simpleDateFormat(dateTimeFormat)
                .serializerByType(LocalDateTime.class,
                        new LocalDateTimeSerializer(
                                DateTimeFormatter.ofPattern(dateTimeFormat)
                        )
                )
                .deserializerByType(LocalDateTime.class,
                        new LocalDateTimeDeserializer(
                                DateTimeFormatter.ofPattern(dateTimeFormat)
                        )
                );
    }

}
