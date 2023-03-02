package com.solvd.micro9.users.web.mapper;

import com.solvd.micro9.users.domain.User;
import com.solvd.micro9.users.web.dto.UserDto;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto domainToDto(User user);

    List<UserDto> domainToDto(List<User> users);

}
