package com.solvd.micro9.users.domain.elasticsearch;

import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElstcUser {

    private String fullName;

    private String phone;

    private int age;

    private Gender gender;

    private float height;

    private float weight;

    private EyesColor eyesColor;

    @Field(type = FieldType.Integer_Range)
    private StudyYears studyYears;

}
