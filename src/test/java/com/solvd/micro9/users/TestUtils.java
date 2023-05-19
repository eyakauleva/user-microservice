package com.solvd.micro9.users;

import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import com.solvd.micro9.users.domain.elasticsearch.StudyYears;

import java.util.List;

public final class TestUtils {

    private TestUtils() {
    }

    public static User getUser() {
        return new User("111", "Liza", "Ya", "email@gmail.com",
                "+12345", 20, Gender.FEMALE, 170.5f,
                50.2f, EyesColor.BLUE, 2010, 2015, false);
    }

    public static ElstcUser getElstcUser() {
        return new ElstcUser("1111", "Liza Ya", "+12345",
                20, Gender.FEMALE, 170.5f, 50.2f, EyesColor.BLUE,
                new StudyYears(2015, 2018));
    }

    public static List<ElstcUser> getElstcUsers() {
        ElstcUser user1 = new ElstcUser("111", "Liza Ya", "+12345",
                20, Gender.FEMALE, 170.5f, 57.2f, EyesColor.BLUE,
                new StudyYears(2019, 2024));
        ElstcUser user2 = new ElstcUser("234", "Ivan Ivanov", "+8928912",
                36, Gender.MALE, 192.4f, 96.2f, EyesColor.GREEN,
                new StudyYears(2010, 2014));
        ElstcUser user3 = new ElstcUser("43436", "Petr Petrov", "+35232",
                27, Gender.UNSET, 179.0f, 85.1f, EyesColor.UNSET,
                new StudyYears(2012, 2016));
        return List.of(user1, user2, user3);
    }

    public static User convertToUser(ElstcUser user) {
        String[] names = user.getFullName().split("\\s+");
        return new User(user.getId(), names[0], names[1], "email@gmail.com",
                user.getPhone(), user.getAge(), user.getGender(), user.getHeight(),
                user.getWeight(), user.getEyesColor(), user.getStudyYears().getGte(),
                user.getStudyYears().getLte(), false);
    }

}
