package com.solvd.micro9.users.persistence.elastic.query.user.field;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.persistence.elastic.query.user.UserField;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class Name implements UserField {

    @Override
    public void apply(final UserCriteria criteriaData, final Criteria searchCriteria) {
        if (criteriaData.getName() != null) {
            searchCriteria.and(Criteria.where("full_name")
                    .contains(criteriaData.getName()));
        }
    }

}
