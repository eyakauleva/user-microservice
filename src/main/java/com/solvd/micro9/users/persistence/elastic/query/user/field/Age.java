package com.solvd.micro9.users.persistence.elastic.query.user.field;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.persistence.elastic.query.user.UserField;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class Age implements UserField {

    @Override
    public void apply(UserCriteria criteriaData, Criteria searchCriteria) {
        if (criteriaData.getAge() != null) {
            searchCriteria.and(Criteria.where("age")
                    .is(criteriaData.getAge()));
        }
    }

}
