package com.solvd.micro9.users;

import com.solvd.micro9.users.domain.aggregate.EyesColor;
import com.solvd.micro9.users.domain.aggregate.Gender;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ESearchUser;
import com.solvd.micro9.users.domain.elasticsearch.StudyYears;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

public final class TestUtils {

    private TestUtils() {
    }

    public static User getUser() {
        return new User("111", "Liza", "Ya", "email@gmail.com",
                "+12345", 20, Gender.FEMALE, 170.5f,
                50.2f, EyesColor.BLUE, 2010, 2015, false);
    }

    public static ESearchUser getElstcUser() {
        return new ESearchUser("1111", "Liza Ya", "+12345",
                20, Gender.FEMALE, 170.5f, 50.2f, EyesColor.BLUE,
                new StudyYears(2015, 2018), LocalDateTime.now());
    }

    public static List<ESearchUser> getElstcUsers() {
        ESearchUser user1 = new ESearchUser("111", "Liza Ya", "+12345",
                20, Gender.FEMALE, 170.5f, 57.2f, EyesColor.BLUE,
                new StudyYears(2019, 2024), LocalDateTime.now());
        ESearchUser user2 = new ESearchUser("234", "Ivan Ivanov", "+8928912",
                36, Gender.MALE, 192.4f, 96.2f, EyesColor.GREEN,
                new StudyYears(2010, 2014), LocalDateTime.now());
        ESearchUser user3 = new ESearchUser("43436", "Petr Petrov", "+35232",
                27, Gender.UNSET, 179.0f, 85.1f, EyesColor.UNSET,
                new StudyYears(2012, 2016), LocalDateTime.now());
        return List.of(user1, user2, user3);
    }

    public static User convertToUser(final ESearchUser user) {
        String[] names = user.getFullName().split("\\s+");
        return new User(user.getId(), names[0], names[1], "email@gmail.com",
                user.getPhone(), user.getAge(), user.getGender(), user.getHeight(),
                user.getWeight(), user.getEyesColor(), user.getStudyYears().getGte(),
                user.getStudyYears().getLte(), false);
    }

    public static UserCriteria getUserCriteria() {
        return new UserCriteria("a", null, 20,
                170f, 190f, 55f, 70f,
                Set.of(Gender.MALE, Gender.FEMALE),
                Set.of(EyesColor.BLUE, EyesColor.UNSET), 2020);
    }

    public static boolean doesESearchUserSatisfyCriteria(
            final ESearchUser eSearchUser, final UserCriteria userCriteria
    ) {
        List<BiPredicate<ESearchUser, UserCriteria>> predicates = new ArrayList<>();
        predicates.add((user, criteria) -> Objects.isNull(criteria.getName())
                || user.getFullName().contains(criteria.getName()));
        predicates.add((user, criteria) -> Objects.isNull(criteria.getPhone())
                || user.getPhone().equals(criteria.getPhone()));
        predicates.add((user, criteria) -> Objects.isNull(criteria.getAge())
                || criteria.getAge().equals(user.getAge()));
        predicates.add((user, criteria) -> Objects.isNull(criteria.getHeightFrom())
                || user.getHeight() >= criteria.getHeightFrom());
        predicates.add((user, criteria) -> Objects.isNull(criteria.getHeightTo())
                || user.getHeight() <= criteria.getHeightTo());
        predicates.add((user, criteria) -> Objects.isNull(criteria.getWeightFrom())
                || user.getWeight() >= criteria.getWeightFrom());
        predicates.add((user, criteria) -> Objects.isNull(criteria.getWeightTo())
                || user.getWeight() <= criteria.getWeightTo());
        predicates.add((user, criteria) -> Objects.isNull(criteria.getGenders())
                || criteria.getGenders().contains(user.getGender()));
        predicates.add((user, criteria) -> Objects.isNull(criteria.getEyesColors())
                || criteria.getEyesColors().contains(user.getEyesColor()));
        predicates.add((user, criteria) -> Objects.isNull(criteria.getStudyYear())
                || (user.getStudyYears().getGte() <= criteria.getStudyYear()
                    && user.getStudyYears().getLte() >= criteria.getStudyYear()));

        BiPredicate<ESearchUser, UserCriteria> predicate
                = predicates.stream().reduce(BiPredicate::and).get();

        return predicate.test(eSearchUser, userCriteria);
    }

}
