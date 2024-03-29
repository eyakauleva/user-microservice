package com.solvd.micro9.users.domain.elasticsearch;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Document(indexName = "syncmongoelastic")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ESearchUser {

    @Id
    private String id;

    @Field(type = FieldType.Text, name = "full_name")
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

    @Field(type = FieldType.Keyword, name = "eyes_color")
    private EyesColor eyesColor;

    @Field(type = FieldType.Integer_Range, name = "study_years")
    private StudyYears studyYears;

    @Field(
            type = FieldType.Date,
            pattern = "yyyy-MM-dd'T'HH:mm:ss",
            format = DateFormat.date_hour_minute_second
    )
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp = LocalDateTime.now();

}
