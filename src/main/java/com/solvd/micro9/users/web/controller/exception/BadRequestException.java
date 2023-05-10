package com.solvd.micro9.users.web.controller.exception;

public class BadRequestException extends RuntimeException {

    private final ExceptionBody exceptionBody;

    public BadRequestException(final ExceptionBody exceptionBody) {
        this.exceptionBody = exceptionBody;
    }

    public ExceptionBody getExceptionBody() {
        return exceptionBody;
    }

}
