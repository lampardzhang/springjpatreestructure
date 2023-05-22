package com.damon.springboot.treestructure.util;

import java.text.*;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class ISO8601WithoutTimeZoneDateFormat extends DateFormat {
    private static final long serialVersionUID = 1L;
    private static final Calendar CALENDAR = new GregorianCalendar();
    private static final NumberFormat NUMBER_FORMAT = new DecimalFormat();

    public ISO8601WithoutTimeZoneDateFormat() {
        this.numberFormat = NUMBER_FORMAT;
        this.calendar = CALENDAR;
    }

    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        String value = ISO8601WithoutTimeZoneUtils.format(date);
        toAppendTo.append(value);
        return toAppendTo;
    }

    public Date parse(String source, ParsePosition pos) {
        try {
            return ISO8601WithoutTimeZoneUtils.parse(source, pos);
        } catch (ParseException var4) {
            return null;
        }
    }

    public Date parse(String source) throws ParseException {
        return ISO8601WithoutTimeZoneUtils.parse(source, new ParsePosition(0));
    }

    public Object clone() {
        return this;
    }

    public String toString() {
        return this.getClass().getName();
    }
}