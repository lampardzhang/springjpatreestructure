package com.damon.springboot.treestructure.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDate;

public class ISO8601LocalDateSerializer extends StdSerializer<LocalDate> {
    public ISO8601LocalDateSerializer() {
        this(null);
    }

    public ISO8601LocalDateSerializer(Class<LocalDate> t) {
        super(t);
    }

    public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider arg2) throws IOException {
        gen.writeString(ISO8601WithoutTimeZoneForLocalDateTimeUtils.format(value));
    }
}
