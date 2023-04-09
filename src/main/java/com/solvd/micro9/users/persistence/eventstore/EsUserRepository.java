package com.solvd.micro9.users.persistence.eventstore;

import com.solvd.micro9.users.domain.es.EsUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface EsUserRepository extends ReactiveMongoRepository<EsUser, Long> {
}
