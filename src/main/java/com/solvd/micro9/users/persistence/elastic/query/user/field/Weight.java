package com.solvd.micro9.users.persistence.elastic.query.user.field;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.persistence.elastic.query.user.UserField;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class Weight implements UserField {

    @Override
    public void apply(final UserCriteria criteriaData, final Criteria searchCriteria) {
        if (criteriaData.getWeightFrom() != null) {
            searchCriteria.and(Criteria.where("weight")
                    .greaterThanEqual(criteriaData.getWeightFrom()));
        }
        if (criteriaData.getWeightTo() != null) {
            searchCriteria.and(Criteria.where("weight")
                    .lessThanEqual(criteriaData.getWeightTo()));
        }
    }

}
