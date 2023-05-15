package com.solvd.micro9.users;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.util.Objects;

public final class TestUtils {

    private TestUtils() {
    }

    private static ObjectMapper objectMapper;

    public static ObjectMapper getObjectMapper() {
        if (Objects.isNull(objectMapper)) {
            objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.setDateFormat(dateFormat);
        }
        return objectMapper;
    }

}
