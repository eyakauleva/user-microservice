package com.solvd.micro9.users.web.controller.exception;

import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleBadRequestException(BadRequestException ex) {
        return ex.getExceptionBody();
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ExceptionBody> ResourceDoesNotExistException(ResourceDoesNotExistException ex) {
        return Mono.just(new ExceptionBody(ex.getMessage()));
    }

    @ExceptionHandler(ServiceIsNotAvailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleServiceIsNotAvailableException(ServiceIsNotAvailableException ex) {
        return new ExceptionBody(ex.getMessage());
    }

}
