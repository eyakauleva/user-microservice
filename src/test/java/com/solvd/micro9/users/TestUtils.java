package com.solvd.micro9.users;

import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import com.solvd.micro9.users.domain.elasticsearch.StudyYears;

public final class TestUtils {

    private TestUtils() {
    }

    public static User getUser() {
        return new User("111", "Liza", "Ya", "email@gmail.com",
                "+12345", 20, Gender.FEMALE, 170.5f,
                50.2f, EyesColor.BLUE, 2010, 2015, false);
    }

    public static ElstcUser getElstcUser() {
        return new ElstcUser("Liza Ya", "+12345",
                20, Gender.FEMALE, 170.5f, 50.2f, EyesColor.BLUE,
                new StudyYears(2015, 2018));
    }

}
