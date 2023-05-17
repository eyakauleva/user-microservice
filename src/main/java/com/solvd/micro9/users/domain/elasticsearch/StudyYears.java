package com.solvd.micro9.users.domain.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyYears {

    //@Field(name = "gte")
    private Integer from;

    //@Field(name = "lte")
    private Integer to;

}
