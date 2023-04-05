package com.solvd.micro9.users.persistence;

import com.solvd.micro9.users.domain.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, Long> {
}
