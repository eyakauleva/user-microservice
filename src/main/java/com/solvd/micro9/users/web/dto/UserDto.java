package com.solvd.micro9.users.web.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDto {

    private String id;

    @NotBlank(message = "User's first name cannot be empty")
    private String firstName;

    @NotBlank(message = "User's last name cannot be empty")
    private String lastName;

    @NotBlank(message = "User's email cannot be empty")
    private String email;

}
