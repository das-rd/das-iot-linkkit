package io.github.dasrd.iot.link.sdk.request;

import io.github.dasrd.iot.link.sdk.utils.JsonUtils;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 设备消息
 */
public class DeviceMessage {
    @JsonProperty("object_device_id")
    private String deviceId;

    /**
     * 消息名，可选
     */
    private String name;

    /**
     * 消息id，可选
     */
    private String id;

    /**
     * 消息具体内容
     */
    private String content;

    /**
     * 默认构造函数
     */
    public DeviceMessage() {

    }

    /**
     * 构造函数
     *
     * @param message 消息内容
     */
    public DeviceMessage(String message) {
        content = message;
    }


    public String getDeviceId() {
        return deviceId;
    }


    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getContent() {
        return content;
    }


    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
