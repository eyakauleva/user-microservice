package com.solvd.micro9.users.service;

import com.solvd.micro9.users.domain.es.Es;
import com.solvd.micro9.users.domain.es.EsType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class ServiceConfig {

    private final UserService userService;

    @Bean
    public Map<EsType, Function<Es, Mono<?>>> dbSynchronizerHandler() {
        Map<EsType, Function<Es, Mono<?>>> operatorMap = new HashMap<>();
        operatorMap.put(EsType.USER_CREATED, userService::create);
        operatorMap.put(EsType.USER_DELETED, userService::delete);
        return operatorMap;
    }

}
