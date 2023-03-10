package com.solvd.micro9.users.web.controller.exception;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.exception.ResourceDoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class ExceptionHandling {

    @ExceptionHandler(HttpClientErrorException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody handleServiceIsNotAvailableException(Exception ex) {
        String json = ex.getMessage().substring(ex.getMessage().indexOf("{"));
        json = json.substring(0, json.lastIndexOf('}') + 1);
        Gson gson = new Gson();
        return gson.fromJson(json, ExceptionBody.class);
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
