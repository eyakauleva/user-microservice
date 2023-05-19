package com.solvd.micro9.users.integration.elastic;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
abstract public class ElasticTestcontainers {

    private static final String TEST_PASSWORD = "testPassword";

    @Container
    public static ElasticsearchContainer container = new ElasticsearchContainer(
            DockerImageName
                    .parse("elasticsearch:8.7.0")
                    .asCompatibleSubstituteFor("docker.elastic.co/elasticsearch/elasticsearch")
    )
            .withPassword(TEST_PASSWORD)
            .withEnv("discovery.type", "single-node")
            .withEnv("xpack.security.enabled", "true");

    @DynamicPropertySource
    static void kafkaProperties(final DynamicPropertyRegistry registry) {
        registry.add("spring.elasticsearch.url",
                container::getHttpHostAddress
        );
        registry.add("spring.elasticsearch.password", () -> TEST_PASSWORD);
    }

}
