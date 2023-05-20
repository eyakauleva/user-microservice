package com.solvd.micro9.users.persistence.elastic.query.user.field;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.persistence.elastic.query.user.UserField;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class Height implements UserField {

    @Override
    public void apply(final UserCriteria criteriaData, final Criteria searchCriteria) {
        if (criteriaData.getHeightFrom() != null) {
            searchCriteria.and(Criteria.where("height")
                    .greaterThanEqual(criteriaData.getHeightFrom()));
        }
        if (criteriaData.getHeightTo() != null) {
            searchCriteria.and(Criteria.where("height")
                    .lessThanEqual(criteriaData.getHeightTo()));
        }
    }

}
