package com.solvd.micro9.users.web.controller.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ExceptionBody {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<BindingError> bindingErrors;

    public ExceptionBody(String message) {
        this.message = message;
    }

}
