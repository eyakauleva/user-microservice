package com.solvd.micro9.users.domain.command;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteUserCommand {

    private String id;
    private String commandBy;

}
