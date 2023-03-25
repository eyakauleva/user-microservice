package com.solvd.micro9.users.service.cache;

import com.solvd.micro9.users.domain.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.*;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisOperations<String, User> reactiveRedisOperations(LettuceConnectionFactory connections) {
        final RedisSerializationContext<String, User> serializationContext = RedisSerializationContext
                .<String, User>newSerializationContext(new StringRedisSerializer())
                .key(new StringRedisSerializer())
                .value(new GenericToStringSerializer<>(User.class))
                .hashKey(new Jackson2JsonRedisSerializer<>(Long.class))
                .hashValue(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(connections, serializationContext);
    }

}
