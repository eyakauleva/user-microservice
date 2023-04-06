package com.solvd.micro9.users.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Transient
    public static final String SEQUENCE_NAME = "users_sequence";

    @Id
    private Long id;

    @Field("first_name")
    private String firstName;

    @Field("last_name")
    private String lastName;

    private String email;

}
