package com.solvd.micro9.users.web.controller;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.command.CreateUserCommand;
import com.solvd.micro9.users.domain.command.DeleteUserCommand;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import com.solvd.micro9.users.service.UserQueryHandler;
import com.solvd.micro9.users.web.dto.UserDto;
import com.solvd.micro9.users.web.dto.criteria.UserCriteriaDto;
import com.solvd.micro9.users.web.mapper.UserCriteriaMapper;
import com.solvd.micro9.users.web.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class GraphqlUserController {

    private final EsUserCommandHandler commandHandler;
    private final UserQueryHandler queryHandler;
    private final UserMapper userMapper;
    private final UserCriteriaMapper criteriaMapper;

    @QueryMapping("getAllUsers")
    public Flux<UserDto> getAll() {
        Flux<User> users = queryHandler.getAll();
        return userMapper.domainToDto(users);
    }

    @QueryMapping("findByCriteria")
    public Flux<UserDto> findByCriteria(@Argument final UserCriteriaDto criteriaDto,
                                        @Argument final int size,
                                        @Argument final int page) {
        UserCriteria criteria = criteriaMapper.dtoToDomain(criteriaDto);
        Pageable pageable = PageRequest.of(page, size);
        Flux<User> userFlux = queryHandler.findByCriteria(criteria, pageable);
        return userMapper.domainToDto(userFlux);
    }

    @QueryMapping("findUserById")
    public Mono<UserDto> findByUserId(@Argument final String userId) {
        EsUserQuery query = new EsUserQuery(userId);
        Mono<User> user = queryHandler.findById(query);
        return userMapper.domainToDto(user);
    }

    @MutationMapping("createUser")
    public Mono<EsUser> create(@Argument final UserDto userDto) {
        User user = userMapper.dtoToDomain(userDto);
        CreateUserCommand command = new CreateUserCommand(user, "Liza123");
        return commandHandler.apply(command);
    }

    @MutationMapping("deleteUser")
    public Mono<EsUser> delete(@Argument final String id) {
        DeleteUserCommand command = new DeleteUserCommand(id, "Liza123");
        return commandHandler.apply(command);
    }

}
