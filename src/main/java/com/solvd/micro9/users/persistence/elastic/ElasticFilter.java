package com.solvd.micro9.users.persistence.elastic;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

public interface ElasticFilter {

    Flux<ElstcUser> doFilter(UserCriteria criteria, Pageable pageable);

}
