package com.solvd.micro9.users.integration.elastic;


import com.solvd.micro9.users.domain.elasticsearch.ESearchUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface ElstcUserRepository extends ElasticsearchRepository<ESearchUser, String> {
}
