package com.solvd.micro9.users.integration.graphql;

import com.solvd.micro9.users.TestUtils;
import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.domain.es.EsUser;
import com.solvd.micro9.users.web.controller.GraphqlUserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.List;
import java.util.stream.IntStream;

@GraphQlTest(GraphqlUserController.class)
@Import(GraphQLITConfig.class)
public class GraphQLIT {

    @Autowired
    private GraphQlTester tester;

    @Test
    void verifyAllUsersAreFoundTest() {
        String query = "{ getAllUsers { id firstName lastName email phone } }";
        List<User> result = this.tester.document(query)
                .execute()
                .path("data.getAllUsers[*]")
                .entityList(User.class)
                .get();
        List<User> users = List.of(TestUtils.getUser());
        assertUsersList(users, result);
    }

    @Test
    void verifyUsersAreFondByCriteriaTest() {
        int size = 10;
        String query = "{ findByCriteria (criteria: {name: \"a\"}, size: " + size
                + ", page: 0) { id firstName lastName email phone } }";
        List<User> result = this.tester.document(query)
                .execute()
                .path("data.findByCriteria[*]")
                .entityList(User.class)
                .get();
        List<User> users = List.of(TestUtils.getUser());
        assertUsersList(users, result);
        Assertions.assertTrue(result.size() <= size);
    }

    @Test
    void verifyUserIsFoundByIdTest() {
        String userId = TestUtils.getUser().getId();
        String query = "{ findUserById (userId: " + userId + ") { id } }";
        User result = this.tester.document(query)
                .execute()
                .path("data.findUserById")
                .entity(User.class)
                .get();
        Assertions.assertNotNull(result);
        Assertions.assertEquals(userId, result.getId());
    }

    @Test
    void verifyUserIsNotFoundByIdTest() {
        String userId = "55555";
        String query = "{ findUserById (userId: " + userId + ") { id } }";
        GraphQlTester.Errors errors = this.tester.document(query)
                .execute()
                .errors()
                .filter(error -> error.getErrorType().equals(ErrorType.BAD_REQUEST));
        Assertions.assertNotNull(errors);
    }

    @Test
    void verifyUserIsCreatedTest() {
        String query = "mutation { createUser (user: { firstName: \"a\" lastName: \"a\" "
                + "email: \"a\" phone: \"555\" }, commandBy: \"Liza\" ) "
                + "{ id type time status createdBy entityId } }";
        EsUser result = this.tester.document(query)
                .execute()
                .path("data.createUser")
                .entity(EsUser.class)
                .get();
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(EsType.USER_CREATED, result.getType());
        Assertions.assertNotNull(result.getTime());
        Assertions.assertEquals(EsStatus.SUBMITTED, result.getStatus());
        Assertions.assertNotNull(result.getCreatedBy());
        Assertions.assertNotNull(result.getEntityId());
    }

    @Test
    void verifyUserIsDeletedTest() {
        String query = "mutation { deleteUser(id: \"1234\", commandBy: \"Liza\" ) "
                + "{ id type time status createdBy entityId } }";
        EsUser result = this.tester.document(query)
                .execute()
                .path("data.deleteUser")
                .entity(EsUser.class)
                .get();
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getId());
        Assertions.assertEquals(EsType.USER_DELETED, result.getType());
        Assertions.assertNotNull(result.getTime());
        Assertions.assertEquals(EsStatus.PENDING, result.getStatus());
        Assertions.assertNotNull(result.getCreatedBy());
        Assertions.assertNotNull(result.getEntityId());
    }

    private void assertUsersList(final List<User> users, final List<User> result) {
        Assertions.assertEquals(users.size(), result.size());
        IntStream.range(0, users.size()).forEach(idx -> {
            Assertions.assertEquals(users.get(idx).getId(), result.get(idx).getId());
            Assertions.assertEquals(
                    users.get(idx).getFirstName(),
                    result.get(idx).getFirstName()
            );
            Assertions.assertEquals(
                    users.get(idx).getLastName(),
                    result.get(idx).getLastName()
            );
            Assertions.assertEquals(users.get(idx).getEmail(), result.get(idx).getEmail());
            Assertions.assertEquals(users.get(idx).getPhone(), result.get(idx).getPhone());
        });
    }

}
