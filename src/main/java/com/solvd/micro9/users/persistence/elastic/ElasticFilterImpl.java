package com.solvd.micro9.users.persistence.elastic;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ESearchUser;
import com.solvd.micro9.users.persistence.elastic.query.QueryBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
@RequiredArgsConstructor
public class ElasticFilterImpl implements ElasticFilter {

    private final ReactiveElasticsearchOperations operations;
    private final QueryBuilder queryBuilder;

    @Override
    public Flux<ESearchUser> doFilter(final UserCriteria criteria,
                                      final Pageable pageable) {
        Query query = queryBuilder.build(criteria, pageable);
        return operations.search(query, ESearchUser.class)
                .map(SearchHit::getContent);
    }

}
