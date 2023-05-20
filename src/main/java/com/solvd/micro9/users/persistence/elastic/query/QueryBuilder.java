package com.solvd.micro9.users.persistence.elastic.query;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Query;

public interface QueryBuilder {

    Query build(UserCriteria criteria, Pageable pageable);

}
