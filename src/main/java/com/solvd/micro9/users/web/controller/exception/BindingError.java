package com.solvd.micro9.users.web.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BindingError {

    private String field;

    private String message;

}
