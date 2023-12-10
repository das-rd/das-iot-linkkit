package io.github.dasrd.iot.link.sdk.request;

import io.github.dasrd.iot.link.sdk.utils.JsonUtils;
import com.fasterxml.jackson.core.type.TypeReference;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 设备消息
 */
public class RawDeviceMessage {
    /**
     * 原始数据
     */
    private byte[] payload;

    private final Set<String> systemMessageKeys = new HashSet<>(
            Arrays.asList("name", "id", "content", "object_device_id"));

    public RawDeviceMessage() {
    }


    public RawDeviceMessage(byte[] payload) {
        this.payload = payload;
    }

    public byte[] getPayload() {
        return payload;
    }

    /**
     * 设置payload
     *
     * @param payload 消息内容
     */
    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public String toUTF8String() {
        return new String(payload, StandardCharsets.UTF_8);
    }

    public DeviceMessage toDeviceMessage() {
        TypeReference<HashMap<String, Object>> typeRef = new TypeReference<HashMap<String, Object>>() {
        };
        Map<String, Object> objectKV = JsonUtils.fromJson(toUTF8String(), typeRef);
        if (objectKV == null) {
            return null; // can't convert to system format
        }
        boolean isSystemFormat = objectKV.entrySet().stream()
                .allMatch(entry -> systemMessageKeys.contains(entry.getKey()));
        if (isSystemFormat) {
            return JsonUtils.fromJson(JsonUtils.toJson(objectKV), DeviceMessage.class);
        }
        return null;
    }
}
