package com.solvd.micro9.users.integration.elastic;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
public abstract class ElasticTestcontainers { //TODO remove PUBLIC

    private static final String TEST_PASSWORD = "testPassword";

    @Container
    private static final ElasticsearchContainer ELASTICSEARCH_CONTAINER =
            new ElasticsearchContainer(
                    DockerImageName
                        .parse("elasticsearch:8.7.0")
                        .asCompatibleSubstituteFor(
                                "docker.elastic.co/elasticsearch/elasticsearch"
                        )
            )
            .withPassword(TEST_PASSWORD)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "true");

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.url",
                ELASTICSEARCH_CONTAINER::getHttpHostAddress
        );
        registry.add("spring.elasticsearch.password", () -> TEST_PASSWORD);
    }

}
