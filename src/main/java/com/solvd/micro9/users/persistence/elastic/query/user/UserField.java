package com.solvd.micro9.users.persistence.elastic.query.user;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import org.springframework.data.elasticsearch.core.query.Criteria;

public interface UserField {

    void apply(UserCriteria criteriaData, Criteria searchCriteria);

}
