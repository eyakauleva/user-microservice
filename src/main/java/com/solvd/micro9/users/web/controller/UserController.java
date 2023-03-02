package com.solvd.micro9.users.web.controller;

import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.service.UserService;
import com.solvd.micro9.users.web.controller.exception.ServiceIsNotAvailableException;
import com.solvd.micro9.users.web.dto.EventDto;
import com.solvd.micro9.users.web.dto.UserDto;
import com.solvd.micro9.users.web.mapper.UserMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;
    private final RestTemplate restTemplate;
    private static final String URL = "http://TICKET-SERVICE/api/v1/events";
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
    @CircuitBreaker(name = USER_SERVICE, fallbackMethod = "sendError")
    public EventDto[] findEventsByUserId(@PathVariable(name = "id") Long userId) {
        String url = URL + "/" + userId;
        return restTemplate.getForObject(url, EventDto[].class);
    }

    private EventDto[] sendError(Exception ex){
        throw new ServiceIsNotAvailableException("Sorry, we have some issues :(");
    }

}
