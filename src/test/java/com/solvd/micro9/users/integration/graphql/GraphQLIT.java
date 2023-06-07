package com.solvd.micro9.users.integration.graphql;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.service.EsUserCommandHandler;
import com.solvd.micro9.users.web.controller.GraphqlUserController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.GraphQlTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;

import java.util.ArrayList;
import java.util.List;


@GraphQlTest(GraphqlUserController.class)
@Import(GraphQLITConfig.class)
public class GraphQLIT {

    @MockBean
    public EsUserCommandHandler commandHandler;

    @Autowired
    private GraphQlTester tester;

    @Test
    void addEmployee() {
        String query = "{ getAllUsers { id firstName } }";
        List users = this.tester.document(query)
                .execute()
                .path("data.getAllUsers[*]")
                .entity(ArrayList.class).get();
        System.out.println("users: " + users);
        Assertions.assertEquals(users.size(), 1);
    }

}
