package com.solvd.micro9.users.web.controller.graphql;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class LocalDateTimeToStringCoercing implements Coercing<LocalDateTime, String> {

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
        return this.parse(input);
    }

    @Override
    public LocalDateTime parseLiteral(final Object input) {
        return this.parse(input);
    }

    private LocalDateTime parse(final Object input) {
        try {
            if (input instanceof StringValue) {
                return LocalDateTime.parse(((StringValue) input).getValue());
            } else if (input instanceof String) {
                return LocalDateTime.parse((String) input);
            } else {
                throw new CoercingParseLiteralException(
                        "Expected String or StringValue"
                );
            }
        } catch (DateTimeParseException e) {
            throw new CoercingParseValueException(
                    String.format("Not a valid date: '%s'.", input), e
            );
        }
    }

}
