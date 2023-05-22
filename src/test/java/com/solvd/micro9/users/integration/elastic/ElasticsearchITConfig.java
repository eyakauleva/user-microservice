package com.solvd.micro9.users.integration.elastic;

import com.solvd.micro9.users.domain.aggregate.User;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import reactor.core.publisher.Mono;

@Configuration
@EnableAutoConfiguration
@ComponentScan("com.solvd.micro9.users.persistence.elastic")
public class ElasticsearchITConfig {

    @Bean
    public ReactiveRedisOperations<String, User> operations() {
        ReactiveRedisOperations<String, User> redisOperations =
                Mockito.mock(ReactiveRedisOperations.class);
        ReactiveHashOperations hashOperations = Mockito.mock(ReactiveHashOperations.class);
        Mockito.when(hashOperations.get(
                Mockito.anyString(), Mockito.anyString())).thenReturn(Mono.empty()
        );
        Mockito.when(hashOperations.put(
                        Mockito.anyString(), Mockito.anyString(), Mockito.any(User.class)
                ))
                .thenReturn(Mono.just(true));
        Mockito.when(redisOperations.opsForHash()).thenReturn(hashOperations);
        return redisOperations;
    }

}
