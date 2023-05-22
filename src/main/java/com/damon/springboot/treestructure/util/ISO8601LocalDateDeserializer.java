package com.damon.springboot.treestructure.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;

public class ISO8601LocalDateDeserializer extends StdDeserializer<LocalDate> {
    public ISO8601LocalDateDeserializer() {
        this(null);
    }

    public ISO8601LocalDateDeserializer(Class<?> vc) {
        super(vc);
    }

    public LocalDate deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {
        String date = jsonparser.getText();

        try {
            return ISO8601WithoutTimeZoneForLocalDateTimeUtils.parse(date).toLocalDate();
        } catch (ParseException var5) {
            throw new RuntimeException(var5);
        }
    }
}
