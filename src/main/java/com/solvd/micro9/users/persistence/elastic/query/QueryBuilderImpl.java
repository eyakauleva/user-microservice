package com.solvd.micro9.users.persistence.elastic.query;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.persistence.elastic.query.user.UserField;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class QueryBuilderImpl implements QueryBuilder {

    private final List<UserField> userFields;

    @Override
    public Query build(UserCriteria criteriaData, Pageable pageable) {
        Criteria criteria = new Criteria();
        userFields.forEach(field -> field.apply(criteriaData, criteria));
        return new CriteriaQuery(criteria).setPageable(pageable);
    }

}
