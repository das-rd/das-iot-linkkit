package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.request.ThingPropertySetResult;

import java.util.Map;

/**
 * 收到属性设置消息处理函数
 *
 * @author tangsq
 */
@FunctionalInterface
public interface OnThingPropertySet {
    /**
     * 收到属性设置消息处理函数
     *
     * @param msgId
     * @param propData
     * @return {@link ThingPropertySetResult}
     */
    ThingPropertySetResult onThingPropertySet(String msgId, Map<String, Object> propData);
}
