package com.damon.springboot.treestructure.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

@JsonComponent
public class WebDateFormat {

    //SimpleDateFormat对象
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



    //格式化日期
    public static class DateFormatSerializer extends JsonSerializer<LocalDateTime> {
        @Override
        public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) {
            try {
                //获取登录账号时区字段，并设置序列化日期格式
                String timeZone = "Asia/Shanghai";//TODO should use login user timezone
                dateTimeFormatter.withZone(ZoneId.of(timeZone));
                gen.writeString(dateTimeFormatter.format(value));

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    //解析日期字符串
    public static class DateParseDeserializer extends JsonDeserializer<LocalDateTime> {
        @Override
        public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {

                //获取登录账号时区字段，并设置序列化日期格式
                String timeZone = "Asia/Shanghai";//TODO should use login user timezone
                dateTimeFormatter.withZone(ZoneId.of(timeZone));
                return LocalDateTime.parse(p.getValueAsString(),dateTimeFormatter);


        }
    }
}