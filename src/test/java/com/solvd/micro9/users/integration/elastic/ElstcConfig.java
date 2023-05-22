package com.solvd.micro9.users.integration.elastic;

import com.solvd.micro9.users.domain.aggregate.User;
import com.solvd.micro9.users.persistence.elastic.ReactiveElasticFilter;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.elasticsearch.client.erhlc.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.ReactiveElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.SimpleElasticsearchMappingContext;
import org.springframework.data.redis.core.ReactiveHashOperations;

@Configuration
@ComponentScan("com.solvd.micro9.users.persistence.elastic")
public class ElstcConfig {

    @Bean
    public ReactiveHashOperations<String, String, User> operations() {
        return Mockito.mock(ReactiveHashOperations.class);
    }

    @Autowired
    public ReactiveElasticsearchOperations elasticFilter;

//    @Bean
//    @Primary
//    public ElasticsearchOperations elasticsearchOperations() {
//        // Define your test-specific Elasticsearch configuration or mock
//        return new ElasticsearchRestTemplate(new RestHighLevelClient(
//                RestClient.builder(new HttpHost("localhost", 9200, "http"))
//                // Customize the client configuration as needed
//        ), new SimpleElasticsearchMappingContext()); // Example: using ElasticsearchRestTemplate
//    }

}
