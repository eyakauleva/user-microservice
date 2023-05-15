package com.solvd.micro9.users.domain.elasticsearch;

import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ElstcUser {

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String fullName;

    @Field(type = FieldType.Keyword)
    private String phone;

    @Field(type = FieldType.Integer)
    private int age;

    @Field(type = FieldType.Keyword)
    private Gender gender;

    @Field(type = FieldType.Float)
    private float height;

    @Field(type = FieldType.Float)
    private float weight;

    @Field(type = FieldType.Keyword)
    private EyesColor eyesColor;

    @Field(type = FieldType.Integer_Range)
    private StudyYears studyYears;

}
