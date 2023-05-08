package com.solvd.micro9.users.web;

import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class WebConfig {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

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

}
