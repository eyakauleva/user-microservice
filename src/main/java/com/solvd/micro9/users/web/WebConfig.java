package com.solvd.micro9.users.web;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import graphql.schema.GraphQLScalarType;
import graphql.schema.CoercingParseLiteralException;
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
import java.time.format.DateTimeParseException;

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

    public GraphQLScalarType localDateTimeScalar() {
        return GraphQLScalarType.newScalar()
                .name("LocalDateTime")
                .description("Java 8 LocalDateTime as scalar")
                .coercing(new Coercing<LocalDateTime, String>() {
                    @Override
                    public String serialize(final Object input) {
                        if (input instanceof LocalDateTime) {
                            DateTimeFormatter formatter =
                                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            return ((LocalDateTime) input).format(formatter);
                        } else {
                            throw new CoercingSerializeException(
                                    "Expected a LocalDateTime object"
                            );
                        }
                    }

                    @Override
                    public LocalDateTime parseValue(final Object input) {
                        try {
                            if (input instanceof String) {
                                return LocalDateTime.parse((String) input);
                            } else {
                                throw new CoercingParseValueException("Expected a String");
                            }
                        } catch (DateTimeParseException e) {
                            throw new CoercingParseValueException(
                                    String.format("Not a valid date: '%s'.", input), e
                            );
                        }
                    }

                    @Override
                    public LocalDateTime parseLiteral(final Object input) {
                        if (input instanceof StringValue) {
                            try {
                                return LocalDateTime.parse(((StringValue) input)
                                        .getValue());
                            } catch (DateTimeParseException e) {
                                throw new CoercingParseLiteralException(e);
                            }
                        } else {
                            throw new CoercingParseLiteralException(
                                    "Expected a StringValue"
                            );
                        }
                    }
                }).build();
    }

    public ValidationSchemaWiring schemaWiring() {
        ValidationRules validationRules = ValidationRules.newValidationRules()
                .onValidationErrorStrategy(OnValidationErrorStrategy.RETURN_NULL)
                .build();
        return new ValidationSchemaWiring(validationRules);
    }

    @Bean
    public RuntimeWiringConfigurer runtimeWiringConfigurer() {
        return wiringBuilder -> wiringBuilder
                .scalar(localDateTimeScalar())
                .directiveWiring(schemaWiring());
    }

}
