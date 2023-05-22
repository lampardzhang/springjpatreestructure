package com.damon.springboot.treestructure.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.io.Writer;

import java.lang.reflect.ParameterizedType;
import java.text.ParseException;
import java.text.ParsePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class JSONUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(JSONUtils.class);
    private static ObjectMapper writerMapper;
    private static ObjectMapper readerMapperForOther;
    private static ObjectMapper readerMapperForDomainModel;
    private static ObjectMapper writeMapperPrintNull;
    private static final Set<Long> type = null;
    private static final String SPACE = "    ";

    public JSONUtils() {
    }

    private static void initMapper() {
        boolean caseInsensitive = Boolean.TRUE;
        writerMapper = new ObjectMapper();
        SimpleModule baseEntityAsKeyModule = new SimpleModule();
        writerMapper.registerModule(baseEntityAsKeyModule);
        writerMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //writerMapper.setDateFormat(new ISO8601WithoutTimeZoneDateFormat());
        writerMapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        writerMapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        writerMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);
        writerMapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);
        writerMapper.configure(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS, true);
        writerMapper.configure(MapperFeature.CAN_OVERRIDE_ACCESS_MODIFIERS, true);
        writerMapper.configure(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS, true);
        writerMapper.configure(com.fasterxml.jackson.core.JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        writerMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, caseInsensitive);
        writerMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        writerMapper.disable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);

        //writerMapper.setPropertyNamingStrategy(new PropertyNamingStrategy.PascalCaseStrategy());
        writerMapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);

        writeMapperPrintNull = writerMapper.copy();
        writeMapperPrintNull.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        writeMapperPrintNull.enable(SerializationFeature.WRITE_EMPTY_JSON_ARRAYS);
        readerMapperForOther = writerMapper.copy();
        SimpleModule simpleModule0 = new SimpleModule();
        simpleModule0.addSerializer(LocalDateTime.class, new ISO8601LocalDateTimeSerializer());
        simpleModule0.addDeserializer(LocalDateTime.class, new ISO8601LocalDateTimeDeserializer());
        simpleModule0.addSerializer(LocalDate.class, new ISO8601LocalDateSerializer());
        simpleModule0.addDeserializer(LocalDate.class, new ISO8601LocalDateDeserializer());
        readerMapperForOther.registerModule(simpleModule0);
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addSerializer(LocalDateTime.class, new ISO8601LocalDateTimeSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new ISO8601LocalDateTimeDeserializer());
        simpleModule.addSerializer(LocalDate.class, new ISO8601LocalDateSerializer());
        simpleModule.addDeserializer(LocalDate.class, new ISO8601LocalDateDeserializer());
        writerMapper.registerModule(simpleModule);
        readerMapperForDomainModel = writerMapper;
    }

    public static ObjectMapper getWriterObjectMapper() {
        return writerMapper;
    }

    public static ObjectMapper getReaderObjectMapper() {
        return readerMapperForOther;
    }

    public static String toJSON(Object object) {
        return toJSON(object, true, false);
    }

    public static String toJSON(Object object, boolean trimEmptyJson, boolean withNullValue) {
        try {
            if (object == null) {
                return null;
            } else {
                preJsonConvert(object);
                ObjectMapper mapper = writerMapper;
                if (withNullValue) {
                    mapper = writeMapperPrintNull;
                }

                String jsonString = mapper.writeValueAsString(object);
                if (trimEmptyJson && ("[]".equals(jsonString) || "{}".equals(jsonString))) {
                    jsonString = null;
                }

                return jsonString;
            }
        } catch (Exception var5) {
            LOGGER.error("toJSON error", var5);
            throw new RuntimeException(var5);
        }
    }

    public static JsonNode toJsonNode(Object object) {
        try {
            preJsonConvert(object);
            return writerMapper.convertValue(object, JsonNode.class);
        } catch (Exception var2) {
            LOGGER.error("toJsonNode error", var2);
            throw new RuntimeException(var2);
        }
    }

    public static String jsonNodeToString(JsonNode jsonNode) {
        try {
            return writerMapper.writeValueAsString(jsonNode);
        } catch (Exception var2) {
            LOGGER.error("jsonNodeToString error", var2);
            throw new RuntimeException(var2);
        }
    }

    public static void writeJsonNodeToStream(Writer Writer, JsonNode jsonNode) {
        try {
            writerMapper.writeValue(Writer, jsonNode);
        } catch (Exception var3) {
            LOGGER.error("writeJsonNodeToStream error", var3);
            throw new RuntimeException(var3);
        }
    }

    public static JsonNode parseJson(String json) {
        try {
            return readerMapperForOther.readTree(json);
        } catch (Exception var2) {
            LOGGER.error("parseJson error", var2);
            throw new RuntimeException(var2);
        }
    }

    public static <T> T fromJSON(String json, Class<T> clazz) {
        ObjectMapper readerMapper = null;

            readerMapper = readerMapperForOther;

        try {
            if (json == null) {
                return null;
            } else {
                Object object = readerMapper.readValue(json, clazz);
                postJsonCovnert(object, false);
                return (T) object;
            }
        } catch (Exception var5) {
            LOGGER.error("parseJson error:" + var5.getMessage() + ",Json data:\n" + json, var5);
            throw new RuntimeException(var5);
        }
    }



    public static <T> T fromJSON(String json, JsonNode jsonNode, Class<T> clazz) {
        ObjectMapper readerMapper = null;

            readerMapper = readerMapperForOther;


        try {
            if (json == null) {
                return null;
            } else {
                Object object = readerMapper.treeToValue(jsonNode, clazz);
                postJsonCovnert(object, false);
                return (T) object;
            }
        } catch (Exception var6) {
            LOGGER.error("parseJson error:" + var6.getMessage() + ",Json data:\n" + json, var6);
            throw new RuntimeException(var6);
        }
    }

    public static Object fromJSON(String json, ParameterizedType type) {
        ObjectMapper readerMapper = null;

            readerMapper = readerMapperForOther;


        int size = type.getActualTypeArguments().length;
        if (size != 1) {
            throw new RuntimeException("the type[" + type.getTypeName() + "] is not supported, only suppprt list/set now");
        } else {
            JavaType javaType = readerMapper.getTypeFactory().constructCollectionType((Class)type.getRawType(), (Class)type.getActualTypeArguments()[0]);

            try {
                Collection<?> list = readerMapper.readValue(json, javaType);
                return list;
            } catch (Exception var6) {
                LOGGER.error("parseJson error:" + var6.getMessage() + ",Json data:\n" + json, var6);
                throw new RuntimeException(var6);
            }
        }
    }

    public static <T> List<T> fromJSONAsList(String json, Class<T> elementClazz) {
        return fromJSONAsList(json, List.class, elementClazz);
    }

    public static <T> List<T> fromJSONAsList(String json, JsonNode jsonNode, Class<T> elementClazz) {
        return fromJSONAsList(json, jsonNode, List.class, elementClazz);
    }

    public static <T> List<T> fromJSONAsList(String json, Class<? extends List> listClazz, Class<T> elementClazz) {
        ObjectMapper readerMapper = null;

            readerMapper = readerMapperForOther;


        if (json == null) {
            return null;
        } else {
            try {
                JavaType type = readerMapper.getTypeFactory().constructCollectionType(listClazz, elementClazz);
                List<T> list = readerMapper.readValue(json, type);
                postJsonCovnert(list, false);
                return list;
            } catch (Exception var6) {
                LOGGER.error("parseJson error:" + var6.getMessage() + ",Json data:\n" + json, var6);
                throw new RuntimeException(var6);
            }
        }
    }

    public static <T> List<T> fromJSONAsList(String json, JsonNode jsonNode, Class<? extends List> listClazz, Class<T> elementClazz) {
        ObjectMapper readerMapper = null;

            readerMapper = readerMapperForOther;


        if (jsonNode == null) {
            return null;
        } else {
            try {
                JavaType type = readerMapper.getTypeFactory().constructCollectionType(listClazz, elementClazz);
                ObjectReader reader = readerMapper.readerFor(type);
                List<T> list = reader.readValue(jsonNode);
                postJsonCovnert(list, false);
                return list;
            } catch (Exception var8) {
                LOGGER.error("parseJson error:" + var8.getMessage() + ",Json data:\n" + json, var8);
                throw new RuntimeException(var8);
            }
        }
    }

    public static <T> Set<T> fromJSONAsSet(String json, Class<T> elementClazz) {
        return fromJSONAsSet(json, Set.class, elementClazz);
    }

    public static <T> Set<T> fromJSONAsSet(String json, JsonNode jsonNode, Class<T> elementClazz) {
        return fromJSONAsSet(json, jsonNode, Set.class, elementClazz);
    }

    public static <T> Set<T> fromJSONAsSet(String json, Class<? extends Set> setClazz, Class<T> elementClazz) {
        ObjectMapper readerMapper = null;

            readerMapper = readerMapperForOther;


        if (json == null) {
            return null;
        } else {
            try {
                JavaType type = readerMapper.getTypeFactory().constructCollectionType(setClazz, elementClazz);
                Set<T> set = readerMapper.readValue(json, type);
                postJsonCovnert(set, false);
                return set;
            } catch (Exception var6) {
                LOGGER.error("parseJson error:" + var6.getMessage() + ",Json data:\n" + json, var6);
                throw new RuntimeException(var6);
            }
        }
    }

    public static <T> Set<T> fromJSONAsSet(String json, JsonNode jsonNode, Class<? extends Set> setClazz, Class<T> elementClazz) {
        ObjectMapper readerMapper;

            readerMapper = readerMapperForOther;


        if (jsonNode == null) {
            return null;
        } else {
            try {
                JavaType type = readerMapper.getTypeFactory().constructCollectionType(setClazz, elementClazz);
                ObjectReader reader = readerMapper.readerFor(type);
                Set<T> set = reader.readValue(jsonNode);
                postJsonCovnert(set, false);
                return set;
            } catch (Exception var8) {
                LOGGER.error("parseJson error:" + var8.getMessage() + ",Json data:\n" + json, var8);
                throw new RuntimeException(var8);
            }
        }
    }

    public static <K, V> Map<K, V> fromJSONAsMap(String json, Class<K> keyClazz, Class<V> valueClazz) {
        return fromJSONAsMap(json, Map.class, keyClazz, valueClazz, null);
    }

    public static <K, V> Map<K, V> fromJSONAsMap(String json, JsonNode jsonNode, Class<K> keyClazz, Class<V> valueClazz) {
        return fromJSONAsMap(json, jsonNode, Map.class, keyClazz, valueClazz, null);
    }

    public static <K, V> Map<K, V> fromJSONAsMap(String json, Class mapClazz, Class<K> keyClazz, Class<V> valueClazz, JavaType originalType) {
        ObjectMapper readerMapper;

            readerMapper = readerMapperForOther;


        if (json == null) {
            return null;
        } else {
            try {
                JavaType keyType = readerMapper.getTypeFactory().constructType(keyClazz);
                Object valueType;
                if (List.class.isAssignableFrom(valueClazz)) {

                    Class elementClazz = originalType.getContentType().getContentType().getRawClass();
                    JavaType elementType = readerMapper.getTypeFactory().constructType(elementClazz);
                    valueType = CollectionType.construct(valueClazz, elementType);
                } else {
                    valueType = readerMapper.getTypeFactory().constructType(valueClazz);
                }

                JavaType type = MapType.construct(mapClazz, keyType, (JavaType)valueType);
                Map<K, V> map = readerMapper.readValue(json, type);
                postJsonCovnert(map, false);
                return map;
            } catch (Exception var10) {
                LOGGER.error("parseJson error:" + var10.getMessage() + ",Json data:\n" + json, var10);
                throw new RuntimeException(var10);
            }
        }
    }

    public static <K, V> Map<K, V> fromJSONAsMap(String json, JsonNode jsonNode, Class mapClazz, Class<K> keyClazz, Class<V> valueClazz, JavaType originalType) {
        ObjectMapper readerMapper;

            readerMapper = readerMapperForOther;


        if (jsonNode == null) {
            return null;
        } else {
            try {
                JavaType keyType = readerMapper.getTypeFactory().constructType(keyClazz);
                Object valueType;
                if (List.class.isAssignableFrom(valueClazz)) {

                    Class elementClazz = originalType.getContentType().getContentType().getRawClass();
                    JavaType elementType = readerMapper.getTypeFactory().constructType(elementClazz);
                    valueType = CollectionType.construct(valueClazz, elementType);
                } else {
                    valueType = readerMapper.getTypeFactory().constructType(valueClazz);
                }

                JavaType type = MapType.construct(mapClazz, keyType, (JavaType)valueType);
                ObjectReader reader = readerMapper.readerFor(type);
                Map<K, V> map = reader.readValue(jsonNode);
                postJsonCovnert(map, false);
                return map;
            } catch (Exception var12) {
                LOGGER.error("parseJson error:" + var12.getMessage() + ",Json data:\n" + json, var12);
                throw new RuntimeException(var12);
            }
        }
    }

    public static <K, V> Map<K, V> fromJSONAsMapForNonDomainModelImpl(String json, Class mapClazz, Class<K> keyClazz, Class<V> valueClazz, JavaType originalType) {
        ObjectMapper readerMapper = readerMapperForOther;
        if (json == null) {
            return null;
        } else {
            try {
                JavaType keyType = readerMapper.getTypeFactory().constructType(keyClazz);
                Object valueType;
                if (List.class.isAssignableFrom(valueClazz)) {

                    Class elementClazz = originalType.getContentType().getContentType().getRawClass();
                    JavaType elementType = readerMapper.getTypeFactory().constructType(elementClazz);
                    valueType = CollectionType.construct(valueClazz, elementType);
                } else {
                    valueType = readerMapper.getTypeFactory().constructType(valueClazz);
                }

                JavaType type = MapType.construct(mapClazz, keyType, (JavaType)valueType);
                Map<K, V> map = readerMapper.readValue(json, type);
                postJsonCovnert(map, false);
                return map;
            } catch (Exception var10) {
                LOGGER.error("parseJson error:" + var10.getMessage() + ",Json data:\n" + json, var10);
                throw new RuntimeException(var10);
            }
        }
    }







    private static void postJsonCovnert(Object object, boolean isDomainModelOrIncludingDomainModel) {

    }

    private static void preJsonConvert(Object object) {


    }

    public static LocalDateTime parseLocalDateTime(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        } else {
            try {
                return ISO8601WithoutTimeZoneForLocalDateTimeUtils.parse(str);
            } catch (ParseException var2) {
                LOGGER.error("parseDate error", var2);
                throw new RuntimeException("Invalid json date format,value:" + str, var2);
            }
        }
    }

    public static Date parseDate(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        } else {
            try {
                return ISO8601WithoutTimeZoneUtils.parse(str, new ParsePosition(0));
            } catch (ParseException var2) {
                LOGGER.error("parseDate error", var2);
                throw new RuntimeException("Invalid json date format,value:" + str, var2);
            }
        }
    }

    public static String formatDate(Date date) {
        return date == null ? null : ISO8601WithoutTimeZoneUtils.format(date);
    }

    public static String formatLocalDateTime(LocalDateTime date) {
        return date == null ? null : ISO8601WithoutTimeZoneForLocalDateTimeUtils.format(date);
    }

    public static String formatDateIncludeZero(Date date) {
        return date == null ? null : ISO8601WithoutTimeZoneUtils.formatIncludeZero(date);
    }

    public static String JsonStringFormat(String json) {
        StringBuffer result = new StringBuffer();
        Integer length = json.length();
        int number = 0;


        for(int i = 0; i < length; ++i) {
            char word = json.charAt(i);
            if (word != '[' && word != '{') {
                if (word != ']' && word != '}') {
                    if (word == ',' && json.charAt(i - 1) == '"') {
                        result.append(word);
                        result.append('\n');
                        result.append(indent(number));
                    } else {
                        result.append(word);
                    }
                } else {
                    result.append('\n');
                    --number;
                    result.append(indent(number));
                    result.append(word);
                    if (i + 1 < length && json.charAt(i + 1) != ',') {
                        result.append('\n');
                    }
                }
            } else {
                if (i - 1 > 0 && json.charAt(i - 1) == ':') {
                    result.append('\n');
                    result.append(indent(number));
                }

                result.append(word);
                result.append('\n');
                ++number;
                result.append(indent(number));
            }
        }

        return result.toString();
    }

    private static String indent(int number) {
        StringBuffer result = new StringBuffer();

        for(int i = 0; i < number; ++i) {
            result.append(SPACE);
        }

        return result.toString();
    }

    static {
        initMapper();
    }
}
