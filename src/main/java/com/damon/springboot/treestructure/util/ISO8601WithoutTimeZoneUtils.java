package com.damon.springboot.treestructure.util;

import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class ISO8601WithoutTimeZoneUtils {
    private static final TimeZone TIMEZONE_DEFAULT = TimeZone.getDefault();

    public ISO8601WithoutTimeZoneUtils() {
    }

    public static String format(Date date) {
        Date roundMsDate = DateUtils.round(date, 13);
        Calendar calendar = new GregorianCalendar(TIMEZONE_DEFAULT);
        calendar.setTime(roundMsDate);
        int capacity = "yyyy-MM-ddThh:mm:ss.sss".length();
        int hour = calendar.get(11);
        int min = calendar.get(12);
        int second = calendar.get(13);
        int ms = calendar.get(14);
        StringBuilder formatted = new StringBuilder(capacity);
        padInt(formatted, calendar.get(1), "yyyy".length());
        formatted.append('-');
        padInt(formatted, calendar.get(2) + 1, "MM".length());
        formatted.append('-');
        padInt(formatted, calendar.get(5), "dd".length());
        if (hour > 0 || min > 0 || second > 0) {
            formatted.append('T');
            padInt(formatted, hour, "hh".length());
            formatted.append(':');
            padInt(formatted, min, "mm".length());
            formatted.append(':');
            padInt(formatted, second, "ss".length());
            if (ms > 0) {
                formatted.append('.');
                padInt(formatted, ms, "sss".length());
            }
        }

        return formatted.toString();
    }

    public static String formatIncludeZero(Date date) {
        Date roundMsDate = DateUtils.round(date, 13);
        Calendar calendar = new GregorianCalendar(TIMEZONE_DEFAULT);
        calendar.setTime(roundMsDate);
        int capacity = "yyyy-MM-ddThh:mm:ss.sss".length();
        int hour = calendar.get(11);
        int min = calendar.get(12);
        int second = calendar.get(13);
        int ms = calendar.get(14);
        StringBuilder formatted = new StringBuilder(capacity);
        padInt(formatted, calendar.get(1), "yyyy".length());
        formatted.append('-');
        padInt(formatted, calendar.get(2) + 1, "MM".length());
        formatted.append('-');
        padInt(formatted, calendar.get(5), "dd".length());
        if (hour >= 0 || min >= 0 || second >= 0) {
            formatted.append('T');
            padInt(formatted, hour, "hh".length());
            formatted.append(':');
            padInt(formatted, min, "mm".length());
            formatted.append(':');
            padInt(formatted, second, "ss".length());
            if (ms > 0) {
                formatted.append('.');
                padInt(formatted, ms, "sss".length());
            }
        }

        return formatted.toString();
    }

    public static Date parse(String date, ParsePosition pos) throws ParseException {
        Exception fail = null;

        try {
            int offset = pos.getIndex();
            int var10001 = offset;
            offset += 4;
            int year = parseInt(date, var10001, offset);
            if (checkOffset(date, offset, '-')) {
                ++offset;
            }

            var10001 = offset;
            offset += 2;
            int month = parseInt(date, var10001, offset);
            if (checkOffset(date, offset, '-')) {
                ++offset;
            }

            var10001 = offset;
            offset += 2;
            int day = parseInt(date, var10001, offset);
            int hour = 0;
            int minutes = 0;
            int seconds = 0;
            int milliseconds = 0;
            boolean hasT = checkOffset(date, offset, 'T');
            if (!hasT && date.length() <= offset) {
                Calendar calendar = new GregorianCalendar(year, month - 1, day);
                pos.setIndex(offset);
                return calendar.getTime();
            }

            if (hasT) {
                ++offset;
                var10001 = offset;
                offset += 2;
                hour = parseInt(date, var10001, offset);
                if (checkOffset(date, offset, ':')) {
                    ++offset;
                }

                var10001 = offset;
                offset += 2;
                minutes = parseInt(date, var10001, offset);
                if (checkOffset(date, offset, ':')) {
                    ++offset;
                }

                char c;
                if (date.length() > offset) {
                    c = date.charAt(offset);
                    if (c != 'Z' && c != '+' && c != '-') {
                        var10001 = offset;
                        offset += 2;
                        seconds = parseInt(date, var10001, offset);
                        if (checkOffset(date, offset, '.')) {
                            ++offset;
                            var10001 = offset;
                            offset += 3;
                            milliseconds = parseInt(date, var10001, offset);
                        }
                    }
                }

                if (date.length() > offset) {
                    c = date.charAt(offset);
                    if (c != 'Z' && c != '+' && c != '-') {
                        throw new IllegalArgumentException("Invalid char: '" + c + "' at pos " + offset + "!");
                    }

                    throw new IllegalArgumentException("Time zone indicator/offset not support! Char 'Z' or '+' or '-' is not valid!");
                }
            }

            LocalDateTime ldt = LocalDateTime.of(year, month, day, hour, minutes, seconds, milliseconds * 1000000);
            pos.setIndex(offset);
            return LocalDateTimeUtils.convertLocalDateTimeToOldDate(ldt);
        } catch (IndexOutOfBoundsException var13) {
            fail = var13;
        } catch (NumberFormatException var14) {
            fail = var14;
        } catch (IllegalArgumentException var15) {
            fail = var15;
        }

        String input = date == null ? null : "\"" + date + "'";
        String msg = fail.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = "(" + fail.getClass().getName() + ")";
        }

        ParseException ex = new ParseException("Failed to parse date [" + input + "]: " + msg, pos.getIndex());
        ex.initCause(fail);
        throw ex;
    }

    private static boolean checkOffset(String value, int offset, char expected) {
        return offset < value.length() && value.charAt(offset) == expected;
    }

    private static int parseInt(String value, int beginIndex, int endIndex) throws NumberFormatException {
        if (beginIndex >= 0 && endIndex <= value.length() && beginIndex <= endIndex) {
            int i = beginIndex;
            int result = 0;
            int digit;
            if (beginIndex < endIndex) {
                i = beginIndex + 1;
                digit = Character.digit(value.charAt(beginIndex), 10);
                if (digit < 0) {
                    throw new NumberFormatException("Invalid number: " + value);
                }

                result = -digit;
            }

            while(i < endIndex) {
                digit = Character.digit(value.charAt(i++), 10);
                if (digit < 0) {
                    throw new NumberFormatException("Invalid number: " + value);
                }

                result *= 10;
                result -= digit;
            }

            return -result;
        } else {
            throw new NumberFormatException(value);
        }
    }

    private static void padInt(StringBuilder buffer, int value, int length) {
        String strValue = Integer.toString(value);

        for(int i = length - strValue.length(); i > 0; --i) {
            buffer.append('0');
        }

        buffer.append(strValue);
    }
}