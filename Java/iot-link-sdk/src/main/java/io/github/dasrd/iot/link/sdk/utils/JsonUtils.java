package io.github.dasrd.iot.link.sdk.utils;

import io.github.dasrd.iot.link.sdk.exception.ApiException;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Json工具类
 */
@Slf4j
public class JsonUtils {

    /**
     * 获取默认ObjectMapper
     *
     * @return ObjectMapper
     **/
    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

    /**
     * 获取序列化时忽略Null的ObjectMapper
     *
     * @return ObjectMapper
     **/
    public static ObjectMapper getNonNullMapper() {
        return NON_NULL_MAPPER;
    }

    // 默认ObjectMapper：反序列化时忽略未知属性
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    // ObjectMapper：反序列化时忽略未知属性；序列化时忽略Null
    private static final ObjectMapper NON_NULL_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL);

    private JsonUtils() {
    }

    public static String toJson(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[json序列化]错误", e);
            throw new ApiException("SERIALIZATION_ERROR", "JSON序列化错误");
        }
    }


    public static String toJsonPrettyPrinter(Object obj) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[json序列化]错误", e);
            throw new ApiException("SERIALIZATION_ERROR", "JSON序列化错误");
        }
    }


    public static String toJsonNonNull(Object obj) {
        try {
            return NON_NULL_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[json序列化]错误", e);
            throw new ApiException("SERIALIZATION_ERROR", "JSON序列化错误");
        }
    }

    public static String toJsonNonNullPrettyPrinter(Object obj) {
        try {
            return NON_NULL_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            log.error("[json序列化]错误", e);
            throw new ApiException("SERIALIZATION_ERROR", "JSON序列化错误");
        }
    }

    public static <T> T fromJson(String jsonStr, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, type);
        } catch (IOException e) {
            log.error("[json反序列化]无效json字符串:{}", jsonStr, e);
            throw new ApiException("INVALID_JSON_STRING", "无效json字符串");
        }
    }

    public static <T> T fromJson(String jsonStr, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(jsonStr, type);
        } catch (IOException e) {
            log.error("[json反序列化]无效json字符串:{}", jsonStr, e);
            throw new ApiException("INVALID_JSON_STRING", "JSON序列化错误");
        }
    }


    public static <T> T fromJson(byte[] jsonBytes, Class<T> type) {
        try {
            return OBJECT_MAPPER.readValue(jsonBytes, type);
        } catch (IOException e) {
            log.error("[json反序列化]无效jsonBytes:{}", new String(jsonBytes, StandardCharsets.UTF_8), e);
            throw new ApiException("INVALID_JSON_BYTES", "无效jsonBytes");
        }
    }

    public static <T> T fromJson(byte[] jsonBytes, TypeReference<T> type) {
        try {
            return OBJECT_MAPPER.readValue(jsonBytes, type);
        } catch (IOException e) {
            log.error("[json反序列化]无效jsonBytes:{}", new String(jsonBytes, StandardCharsets.UTF_8), e);
            throw new ApiException("INVALID_JSON_BYTES", "无效jsonBytes");
        }
    }


    public static boolean isValidJson(String jsonStr) {
        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(jsonStr);
            if (jsonNode instanceof ObjectNode || jsonNode instanceof ArrayNode) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public static JsonNode readTree(String jsonStr) {
        try {
            return OBJECT_MAPPER.readTree(jsonStr);
        } catch (IOException e) {
            log.error("[json反序列化]无效json字符串:{}", jsonStr, e);
            throw new ApiException("INVALID_JSON_STRING", "无效json字符串");
        }
    }

    public static <T> T treeToValue(JsonNode jsonNode, Class<T> toValueType) {
        try {
            return OBJECT_MAPPER.treeToValue(jsonNode, toValueType);
        } catch (JsonProcessingException e) {
            log.error("[jsonNode转对象]错误", e);
            throw new ApiException("JSON_NODE_TO_OBJ_ERROR", "JsonNode转为对象失败");
        }
    }

    public static <T> T convertValue(Object fromValue, Class<T> toValueType) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueType);
    }

    public static <T> T convertValue(Object fromValue, TypeReference<T> toValueTypeRef) {
        return OBJECT_MAPPER.convertValue(fromValue, toValueTypeRef);
    }

    public static ObjectNode createNode() {
        return OBJECT_MAPPER.createObjectNode();
    }


}
