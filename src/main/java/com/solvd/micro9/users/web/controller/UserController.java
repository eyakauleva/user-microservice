package com.solvd.micro9.users.web.controller;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.service.UserService;
import com.solvd.micro9.users.web.controller.exception.BadRequestException;
import com.solvd.micro9.users.web.controller.exception.ExceptionBody;
import com.solvd.micro9.users.web.controller.exception.ServiceIsNotAvailableException;
import com.solvd.micro9.users.web.dto.EventDto;
import com.solvd.micro9.users.web.dto.TicketDto;
import com.solvd.micro9.users.web.dto.UserDto;
import com.solvd.micro9.users.web.mapper.UserMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    @Value("${ticket-service}")
    private String ticketService;

    private final UserService userService;
    private final UserMapper userMapper;
    private final WebClient.Builder webClientBuilder;
    private static final String USER_SERVICE = "user-service";

    @GetMapping
    public Flux<UserDto> getAll() {
        Flux<User> users = userService.getAll();
        return userMapper.domainToDto(users);
    }

    @GetMapping(value = "/{id}")
    public Mono<UserDto> findByUserId(@PathVariable(name = "id") Long userId) {
        Mono<User> user = userService.findById(userId);
        return userMapper.domainToDto(user);
    }

    @GetMapping(value = "/{id}/events")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fluxCircuitBreakerFallback")
    public Flux<EventDto> findEventsByUserId(@PathVariable(name = "id") Long userId) {
        String url = "http://" + ticketService + "/api/v1/events/user/" + userId;
        return webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EventDto.class);
    }

    @PostMapping(value = "/{id}/tickets")
    @ResponseStatus(HttpStatus.CREATED)
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "monoCircuitBreakerFallback")
    public Mono<TicketDto> createTicket(@PathVariable(name = "id") Long userId, @RequestBody TicketDto ticketDto) {
        String url = "http://" + ticketService + "/api/v1/tickets";
        ticketDto.setUserId(userId);
        return webClientBuilder.build()
                .post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(ticketDto), TicketDto.class)
                .retrieve()
                .onStatus(
                        status -> status.equals(HttpStatus.BAD_REQUEST),
                        (ex) ->
                                ex.bodyToMono(String.class)
                                        .handle(
                                                (body, handler) -> {
                                                    Gson gson = new Gson();
                                                    ExceptionBody exceptionBody = gson.fromJson(body, ExceptionBody.class);
                                                    handler.error(new BadRequestException(exceptionBody));
                                                }))
                .bodyToMono(TicketDto.class);
    }

    @PostMapping
    public Mono<User> create(@RequestBody @Validated UserDto userDto){
        User user = userMapper.dtoToDomain(userDto);
        return userService.create(user);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Void> delete(@PathVariable("id") Long id) {
        return userService.delete(id);
    }

    @SneakyThrows
    private Mono<?> monoCircuitBreakerFallback(Exception ex) {
        if (ex instanceof BadRequestException) throw ex;
        else throw new ServiceIsNotAvailableException("Sorry, we have some issues :(");
    }

    private Flux<?> fluxCircuitBreakerFallback(Exception ex) {
        throw new ServiceIsNotAvailableException("Sorry, we have some issues :(");
    }

}
