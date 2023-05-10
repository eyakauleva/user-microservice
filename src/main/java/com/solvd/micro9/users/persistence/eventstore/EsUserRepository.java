package com.solvd.micro9.users.persistence.eventstore;

import com.solvd.micro9.users.domain.es.EsStatus;
import com.solvd.micro9.users.domain.es.EsType;
import com.solvd.micro9.users.domain.es.EsUser;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.query.Param;
import reactor.core.publisher.Flux;

public interface EsUserRepository extends ReactiveMongoRepository<EsUser, Long> {

    @Query("{'entityId' : :#{#entityId}, 'type' : :#{#type}, 'status' : :#{#status} }")
    Flux<EsUser> findByEntityIdTypeStatus(@Param("entityId") String entityId,
                                          @Param("type") EsType type,
                                          @Param("status") EsStatus status);

    @Query("{$and : [ "
            + "{entityId : ?0}, "
            + "{type : ?1}, "
            + "{ $or : [{status: 'SUBMITTED'}, {status : 'CANCELED'}] } ]}")
    Flux<EsUser> findByEntityIdTypeStatus(String entityId, EsType type);

}
