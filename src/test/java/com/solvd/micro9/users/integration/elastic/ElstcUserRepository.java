package com.solvd.micro9.users.integration.elastic;

import com.solvd.micro9.users.domain.elasticsearch.ESearchUser;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

@ConditionalOnBean(ElasticsearchITConfig.class)
public interface ElstcUserRepository extends ElasticsearchRepository<ESearchUser, String> {
}
