package com.damon.springboot.treestructure.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ISO8601WithoutTimeZoneForLocalDateTimeUtils {
    public static String format(LocalDateTime date) {
        int capacity = "yyyy-MM-ddThh:mm:ss.sss".length();
        int hour = date.getHour();
        int min = date.getMinute();
        int second = date.getSecond();
        int ms = date.getNano() / 1000000;
        StringBuilder formatted = new StringBuilder(capacity);
        padInt(formatted, date.getYear(), "yyyy".length());
        formatted.append('-');
        padInt(formatted, date.getMonthValue(), "MM".length());
        formatted.append('-');
        padInt(formatted, date.getDayOfMonth(), "dd".length());
        if (hour > 0 || min > 0 || second > 0 || ms > 0) {
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

    public static String format(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public static String formatIncludeZero(LocalDateTime date) {
        int capacity = "yyyy-MM-ddThh:mm:ss.sss".length();
        int hour = date.getHour();
        int min = date.getMinute();
        int second = date.getSecond();
        int ms = date.getNano() * 1000000;
        StringBuilder formatted = new StringBuilder(capacity);
        padInt(formatted, date.getYear(), "yyyy".length());
        formatted.append('-');
        padInt(formatted, date.getMonthValue(), "MM".length());
        formatted.append('-');
        padInt(formatted, date.getDayOfMonth(), "dd".length());
        if (hour >= 0 || min >= 0 || second >= 0 || ms > 0) {
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

    public static LocalDateTime parse(String date) throws ParseException {
        if (StringUtils.isBlank(date)) {
            return null;
        } else {
            int offset = 0;
            Exception fail = null;

            try {
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
                int nanoseconds = 0;
                boolean hasT = checkOffset(date, offset, 'T');
                int length = date.length();
                LocalDateTime ldt;
                if (!hasT && length <= offset) {
                    ldt = LocalDateTime.of(year, month, day, 0, 0);
                    return ldt;
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
                    if (length > offset) {
                        c = date.charAt(offset);
                        if (c != 'Z' && c != '+' && c != '-') {
                            var10001 = offset;
                            offset += 2;
                            seconds = parseInt(date, var10001, offset);
                            if (checkOffset(date, offset, '.')) {
                                if (length == 23) {
                                    ++offset;
                                    var10001 = offset;
                                    offset += 3;
                                    nanoseconds = parseInt(date, var10001, offset) * 1000000;
                                } else if (length == 29) {
                                    ++offset;
                                    var10001 = offset;
                                    offset += 9;
                                    nanoseconds = parseInt(date, var10001, offset);
                                }
                            }
                        }
                    }

                    if (length > offset) {
                        c = date.charAt(offset);
                        if (c != 'Z' && c != '+' && c != '-') {
                            throw new IllegalArgumentException("Invalid char: '" + c + "' at pos " + offset + "!");
                        }

                        throw new IllegalArgumentException("Time zone indicator/offset not support! Char 'Z' or '+' or '-' is not valid!");
                    }
                }

                ldt = LocalDateTime.of(year, month, day, hour, minutes, seconds, nanoseconds);
                return ldt;
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

            ParseException ex = new ParseException("Failed to parse date [" + input + "]: " + msg, offset);
            ex.initCause(fail);
            throw ex;
        }
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
