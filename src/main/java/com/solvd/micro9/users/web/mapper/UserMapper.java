package com.solvd.micro9.users.web.mapper;

import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.web.dto.UserDto;
import org.mapstruct.Mapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto domainToDto(User user);

    default Mono<UserDto> domainToDto(Mono<User> userMono) {
        return userMono.map(this::domainToDto);
    }

    default Flux<UserDto> domainToDto(Flux<User> userFlux) {
        return userFlux.map(this::domainToDto);
    }

    User dtoToDomain(UserDto userDto);

}
