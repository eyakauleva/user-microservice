package com.solvd.micro9.users.web.controller;

import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.service.UserService;
import com.solvd.micro9.users.web.controller.exception.ServiceIsNotAvailableException;
import com.solvd.micro9.users.web.controller.openfeign.TicketClient;
import com.solvd.micro9.users.web.dto.EventDto;
import com.solvd.micro9.users.web.dto.TicketDto;
import com.solvd.micro9.users.web.dto.UserDto;
import com.solvd.micro9.users.web.mapper.UserMapper;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@EnableFeignClients
@RequiredArgsConstructor
public class UserController {

    public static final String TICKET_SERVICE = "TICKET-SERVICE";
    private final UserService userService;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private final TicketClient ticketClient;
    private static final String FIND_EVENTS_BY_USER_ID_URL = "http://" + TICKET_SERVICE + "/api/v1/events/user/";
    private static final String USER_SERVICE = "user-service";

    @GetMapping
    public List<UserDto> getAll() {
        List<User> users = userService.getAll();
        return userMapper.domainToDto(users);
    }

    @GetMapping(value = "/{id}")
    public UserDto findByUserId(@PathVariable(name = "id") Long userId) {
        User user = userService.findById(userId);
        return userMapper.domainToDto(user);
    }

    @GetMapping(value = "/{id}/events")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "circuitBreakerFallbackMethod")
    public ResponseEntity<EventDto[]> findEventsByUserId(@PathVariable(name = "id") Long userId) {
        String url = FIND_EVENTS_BY_USER_ID_URL + "/" + userId;
        EventDto[] events = restTemplate.getForObject(url, EventDto[].class);
        return new ResponseEntity<>(events, HttpStatus.CREATED);
    }

    @PostMapping(value = "/{id}/tickets")
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "circuitBreakerFallbackMethod")
    public ResponseEntity<TicketDto> createTicket(@PathVariable(name = "id") Long userId, @RequestBody TicketDto ticketDto) {
        ticketDto.setUserId(userId);
        ticketDto = ticketClient.create(ticketDto);
        return new ResponseEntity<>(ticketDto, HttpStatus.CREATED);
    }

    private ResponseEntity<Void> circuitBreakerFallbackMethod(HttpClientErrorException ex) {
        throw ex;
    }

    private ResponseEntity<Void> circuitBreakerFallbackMethod(FeignException ex) {
        throw ex;
    }

    private ResponseEntity<Void> circuitBreakerFallbackMethod(Exception ex) {
        throw new ServiceIsNotAvailableException("Sorry, we have some issues :(");
    }

}
