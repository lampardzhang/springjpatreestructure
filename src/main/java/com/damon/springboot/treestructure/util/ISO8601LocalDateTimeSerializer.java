package com.damon.springboot.treestructure.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;

public class ISO8601LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {
    public ISO8601LocalDateTimeSerializer() {
        this(null);
    }

    public ISO8601LocalDateTimeSerializer(Class<LocalDateTime> t) {
        super(t);
    }

    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(ISO8601WithoutTimeZoneForLocalDateTimeUtils.format(value));
    }
}