package com.solvd.micro9.users.domain.command;

import com.solvd.micro9.users.domain.aggregate.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateUserCommand {

    private User user;
    private String commandBy;

}
