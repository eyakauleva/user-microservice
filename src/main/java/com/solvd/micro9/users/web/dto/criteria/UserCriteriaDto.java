package com.solvd.micro9.users.web.dto.criteria;

import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCriteriaDto {

    private Integer page;
    private Integer size;
    private String name;
    private String phone;
    private Integer age;
    private Float heightFrom;
    private Float heightTo;
    private Float weightFrom;
    private Float weightTo;
    private Set<Gender> genders;
    private Set<EyesColor> eyesColors;
    private Integer studyYear;

}
