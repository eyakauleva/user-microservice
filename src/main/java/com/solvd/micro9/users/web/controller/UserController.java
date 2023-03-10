package com.solvd.micro9.users.web.controller;

import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.service.UserService;
import com.solvd.micro9.users.web.controller.exception.ServiceIsNotAvailableException;
import com.solvd.micro9.users.web.dto.EventDto;
import com.solvd.micro9.users.web.dto.TicketDto;
import com.solvd.micro9.users.web.dto.UserDto;
import com.solvd.micro9.users.web.mapper.UserMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    public static final String TICKET_SERVICE = "TICKET-SERVICE";
    private final UserService userService;
    private final UserMapper userMapper;
    private final WebClient.Builder webClientBuilder;
    private static final String FIND_EVENTS_BY_USER_ID_URL = "http://" + TICKET_SERVICE + "/api/v1/events/user/";
    private static final String CREATE_TICKET_URL = "http://" + TICKET_SERVICE + "/api/v1/tickets";
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
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "circuitBreakerFallbackMethod")
    public ResponseEntity<Flux<EventDto>> findEventsByUserId(@PathVariable(name = "id") Long userId) {
        String url = FIND_EVENTS_BY_USER_ID_URL + "/" + userId;
        Flux<EventDto> eventDtoFlux = webClientBuilder.build()
                .get()
                .uri(url)
                .retrieve()
                .bodyToFlux(EventDto.class);
        return new ResponseEntity<>(eventDtoFlux, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/tickets")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "circuitBreakerFallbackMethod")
    public ResponseEntity<Mono<TicketDto>> createTicket(@PathVariable(name = "id") Long userId, @RequestBody TicketDto ticketDto) {
        ticketDto.setUserId(userId);
        Mono<TicketDto> ticketDtoMono = webClientBuilder.build()
                .post()
                .uri(CREATE_TICKET_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(ticketDto), TicketDto.class)
                .retrieve()
                .bodyToMono(TicketDto.class);
        return new ResponseEntity<>(ticketDtoMono, HttpStatus.CREATED);
    }

    private ResponseEntity<Void> circuitBreakerFallbackMethod(HttpClientErrorException ex) {
        throw ex;
    }

    private ResponseEntity<Void> circuitBreakerFallbackMethod(Exception ex) {
        throw new ServiceIsNotAvailableException("Sorry, we have some issues :(");
    }

}
