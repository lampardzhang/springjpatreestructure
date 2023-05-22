package com.damon.springboot.treestructure.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;

public class ISO8601LocalDateTimeDeserializer extends StdDeserializer<LocalDateTime> {
    public ISO8601LocalDateTimeDeserializer() {
        this(null);
    }

    public ISO8601LocalDateTimeDeserializer(Class<?> vc) {
        super(vc);
    }

    public LocalDateTime deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        String date = jsonparser.getText();

        try {
            return ISO8601WithoutTimeZoneForLocalDateTimeUtils.parse(date);
        } catch (ParseException var5) {
            throw new RuntimeException(var5);
        }
    }
}
