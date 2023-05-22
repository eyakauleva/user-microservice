package com.solvd.micro9.users.domain.aggregate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements Persistable<String> {

    @Id
    private String id;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    private String email;

    private String phone;

    private int age;

    private Gender gender;

    private float height;

    private float weight;

    @Field("eyes_color")
    private EyesColor eyesColor;

    @Field("start_study_year")
    private int startStudyYear;

    @Field("end_study_year")
    private int endStudyYear;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }

}
