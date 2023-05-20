package com.solvd.micro9.users.persistence.elastic.query.user.field;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.persistence.elastic.query.user.UserField;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.stereotype.Component;

@Component
public class StudyYear implements UserField {

    @Override
    public void apply(UserCriteria criteriaData, Criteria searchCriteria) {
        if (criteriaData.getStudyYear() != null) {
            searchCriteria.and(Criteria.where("study_years")
                    .is(criteriaData.getStudyYear()));
        }
    }

}
