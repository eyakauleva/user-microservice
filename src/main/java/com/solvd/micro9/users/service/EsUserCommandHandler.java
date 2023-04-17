package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.command.CompleteTransactionCommand;
import com.solvd.micro9.users.domain.command.CreateUserCommand;
import com.solvd.micro9.users.domain.command.DeleteUserCommand;
import com.solvd.micro9.users.domain.es.EsUser;
import reactor.core.publisher.Mono;

public interface EsUserCommandHandler {

    Mono<EsUser> apply(CreateUserCommand command);

    Mono<EsUser> apply(DeleteUserCommand command);

    void apply(CompleteTransactionCommand command);

}
