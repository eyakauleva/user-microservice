package com.solvd.micro9.users.domain.elasticsearch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudyYears {

    private Integer gte;
    private Integer lte;

}
