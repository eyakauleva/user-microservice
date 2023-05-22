package com.solvd.micro9.users.web.dto;

import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
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

    @NotBlank(message = "User's phone cannot be empty")
    private String phone;

    private int age;

    private Gender gender;

    private float height;

    private float weight;

    private EyesColor eyesColor;

    private int startStudyYear;

    private int endStudyYear;

}
