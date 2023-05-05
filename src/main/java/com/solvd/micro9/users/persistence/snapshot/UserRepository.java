package com.solvd.micro9.users.persistence.snapshot;

import com.solvd.micro9.users.domain.aggregate.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
}
