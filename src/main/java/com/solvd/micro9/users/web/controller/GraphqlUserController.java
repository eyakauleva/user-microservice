package com.solvd.micro9.users.web.controller;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.command.CreateUserCommand;
import com.solvd.micro9.users.domain.command.DeleteUserCommand;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import com.solvd.micro9.users.service.UserQueryHandler;
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
    public Flux<User> getAll() {
        return queryHandler.getAll();
    }

    @QueryMapping("findByCriteria")
    public Flux<User> findByCriteria(@Argument final UserCriteria criteria,
                                        @Argument final int size,
                                        @Argument final int page) {
        Pageable pageable = PageRequest.of(page, size);
        return queryHandler.findByCriteria(criteria, pageable);
    }

    @QueryMapping("findUserById")
    public Mono<User> findByUserId(@Argument final String userId) {
        EsUserQuery query = new EsUserQuery(userId);
        return queryHandler.findById(query);
    }

    @MutationMapping("createUser")
    public Mono<EsUser> create(@Argument final User user) {
        CreateUserCommand command = new CreateUserCommand(user, "Liza123");
        return commandHandler.apply(command);
    }

    @MutationMapping("deleteUser")
    public Mono<EsUser> delete(@Argument final String id) {
        DeleteUserCommand command = new DeleteUserCommand(id, "Liza123");
        return commandHandler.apply(command);
    }

}
