package com.solvd.micro9.users.web;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.solvd.micro9.users.web.controller.graphql.LocalDateTimeToStringCoercing;
import graphql.schema.GraphQLScalarType;
import graphql.validation.rules.OnValidationErrorStrategy;
import graphql.validation.rules.ValidationRules;
import graphql.validation.schemawiring.ValidationSchemaWiring;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.ReactivePageableHandlerMethodArgumentResolver;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class WebConfig implements WebFluxConfigurer {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    @Override
    public void configureArgumentResolvers(final ArgumentResolverConfigurer configurer) {
        configurer.addCustomResolver(new ReactivePageableHandlerMethodArgumentResolver());
    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomizer() {
        return builder -> builder.simpleDateFormat(DATE_TIME_FORMAT)
                .serializerByType(LocalDateTime.class,
                        new LocalDateTimeSerializer(
                                DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
                        )
                )
                .deserializerByType(LocalDateTime.class,
                        new LocalDateTimeDeserializer(
                                DateTimeFormatter.ofPattern(DATE_TIME_FORMAT)
                        )
                );
    }

    @Bean
    public WebClient.Builder webClient() {
        return WebClient.builder();
    }

    @Bean
    public GraphQLScalarType localDateTimeScalar(
            final LocalDateTimeToStringCoercing coercing
    ) {
        return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Java 8 LocalDateTime as scalar")
                .coercing(coercing)
                .build();
    }

    @Bean
    public ValidationSchemaWiring schemaWiring() {
        ValidationRules validationRules = ValidationRules.newValidationRules()
                .onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL)
                .build();
        return new ValidationSchemaWiring(validationRules);
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer(
            final GraphQLScalarType scalarType,
            final ValidationSchemaWiring schemaWiring
    ) {
        return wiringBuilder -> wiringBuilder
                .scalar(scalarType)
                .directiveWiring(schemaWiring);
    }

}
