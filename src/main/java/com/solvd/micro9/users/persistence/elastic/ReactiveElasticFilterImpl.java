package com.solvd.micro9.users.persistence.elastic;

import com.solvd.micro9.users.domain.criteria.UserCriteria;
import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
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
public class ReactiveElasticFilterImpl implements ReactiveElasticFilter {

    private final ReactiveElasticsearchOperations operations;
    private final QueryBuilder queryBuilder;

    @Override
    public Flux<ElstcUser> doFilter(UserCriteria criteria, Pageable pageable) {
        Query query = queryBuilder.build(criteria, pageable);
        return operations.search(query, ElstcUser.class)
                .map(SearchHit::getContent);
    }

}
