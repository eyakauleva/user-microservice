package com.solvd.micro9.users.integration.elastic;


import com.solvd.micro9.users.domain.elasticsearch.ElstcUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElstcUserRepository extends ElasticsearchRepository<ElstcUser, String> {
}
