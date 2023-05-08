package com.solvd.micro9.users.domain.exception;

public class ResourceDoesNotExistException extends RuntimeException {

    public ResourceDoesNotExistException(final String message) {
        super(message);
    }

}
