package com.damon.springboot.treestructure.util;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class LocalDateTimeUtils {
    public LocalDateTimeUtils() {
    }

    public static Long convertToLong(LocalDateTime date) {
        Instant instant = date.atZone(ZoneId.systemDefault()).toInstant();
        return instant.toEpochMilli();
    }

    public static Date convertLocalDateTimeToOldDate(LocalDateTime date) {
        return Date.from(date.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime convertOldDateTimeToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static LocalDateTime parseDateWithISO8601Format(String dateStr) {
        try {
            return ISO8601WithoutTimeZoneForLocalDateTimeUtils.parse(dateStr);
        } catch (ParseException var2) {
            throw new RuntimeException(var2);
        }
    }
}