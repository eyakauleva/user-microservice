package com.solvd.micro9.users.web.controller;

import com.google.gson.Gson;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.command.CreateUserCommand;
import com.solvd.micro9.users.domain.command.DeleteUserCommand;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.domain.query.EsUserQuery;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import com.solvd.micro9.users.service.UserQueryHandler;
import com.solvd.micro9.users.web.controller.exception.BadRequestException;
import com.solvd.micro9.users.web.controller.exception.ExceptionBody;
import com.solvd.micro9.users.web.controller.exception.ServiceIsNotAvailableException;
import com.solvd.micro9.users.web.dto.EsDto;
import com.solvd.micro9.users.web.dto.EventDto;
import com.solvd.micro9.users.web.dto.TicketDto;
import com.solvd.micro9.users.web.dto.UserDto;
import com.solvd.micro9.users.web.dto.criteria.UserCriteriaDto;
import com.solvd.micro9.users.web.mapper.UserCriteriaMapper;
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
    private final EsUserCommandHandler commandHandler;
    private final UserQueryHandler queryHandler;
    private final UserMapper userMapper;
    private final WebClient.Builder webClientBuilder;
    private final UserCriteriaMapper criteriaMapper;
    private static final String USER_SERVICE = "user-service";

    @GetMapping
    public Flux<UserDto> getAll() {
        Flux<User> users = queryHandler.getAll();
        return userMapper.domainToDto(users);
    }

    @GetMapping(value = "/search")
    public Flux<UserDto> findByCriteria(UserCriteriaDto criteriaDto) {
        UserCriteria criteria = criteriaMapper.dtoToDomain(criteriaDto);
        Flux<User> userFlux = queryHandler.findByCriteria(criteria);
        return userMapper.domainToDto(userFlux);
    }

    @GetMapping(value = "/{id}")
    public Mono<UserDto> findByUserId(@PathVariable(name = "id") final String userId) {
        EsUserQuery query = new EsUserQuery(userId);
        Mono<User> user = queryHandler.findById(query);
        return userMapper.domainToDto(user);
    }

    @GetMapping(value = "/{id}/events")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "fluxCircuitBreakerFallback")
    public Flux<EventDto> findEventsByUserId(
            @PathVariable(name = "id") final String userId
    ) {
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
    public Mono<EsDto> createTicket(
            @PathVariable(name = "id") final String userId,
            @RequestBody final TicketDto ticketDto
    ) {
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
                        (ex) -> ex.bodyToMono(String.class).handle(
                                (body, handler) -> {
                                    ExceptionBody exceptionBody = new Gson().fromJson(
                                            body, ExceptionBody.class
                                    );
                                    handler.error(new BadRequestException(exceptionBody));
                                }
                        )
                )
                .bodyToMono(EsDto.class);
    }

    @PostMapping
    public Mono<EsUser> create(@RequestBody @Validated final UserDto userDto) {
        User user = userMapper.dtoToDomain(userDto);
        CreateUserCommand command = new CreateUserCommand(user, "Liza123");
        return commandHandler.apply(command);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<EsUser> delete(@PathVariable("id") final String id) {
        DeleteUserCommand command = new DeleteUserCommand(id, "Liza123");
        return commandHandler.apply(command);
    }

    @SneakyThrows
    private Mono<?> monoCircuitBreakerFallback(final Exception ex) {
        if (ex instanceof BadRequestException) {
            throw ex;
        } else {
            throw new ServiceIsNotAvailableException("Sorry, we have some issues :(");
        }
    }

    private Flux<?> fluxCircuitBreakerFallback(final Exception ex) {
        throw new ServiceIsNotAvailableException("Sorry, we have some issues :(");
    }

}
