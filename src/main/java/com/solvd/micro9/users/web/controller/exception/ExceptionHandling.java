package com.solvd.micro9.users.web.controller.exception;

import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(WebExchangeBindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ExceptionBody> handleException(
            final WebExchangeBindException ex
    ) {
        List<BindingError> bindingErrors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(fieldError ->
                        new BindingError(
                                fieldError.getObjectName()
                                        + "."
                                        + ((FieldError) fieldError).getField(),
                                fieldError.getDefaultMessage()))
                .collect(Collectors.toList());
        return Mono.just(new ExceptionBody("Binding errors", bindingErrors));
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleBadRequestException(
            final BadRequestException ex
    ) {
        return ex.getExceptionBody();
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Mono<ExceptionBody> handleResourceDoesNotExistException(
            final ResourceDoesNotExistException ex
    ) {
        return Mono.just(new ExceptionBody(ex.getMessage()));
    }

    @ExceptionHandler(ServiceIsNotAvailableException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionBody handleServiceIsNotAvailableException(
            final ServiceIsNotAvailableException ex
    ) {
        return new ExceptionBody(ex.getMessage());
    }

}
