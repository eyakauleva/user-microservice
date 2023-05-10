package com.solvd.micro9.users.web.controller.exception;

public class ServiceIsNotAvailableException extends RuntimeException {

    public ServiceIsNotAvailableException(final String message) {
        super(message);
    }

}
