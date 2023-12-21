package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.request.ThingServiceCallResult;

import java.util.Map;

/**
 * 收到服务下发处理函数
 *
 * @author tangsq
 */
@FunctionalInterface
public interface OnThingServiceCall {
    /**
     * 收到服务下发处理函数
     *
     * @param msgId
     * @param identifier
     * @param inputData
     * @return {@link ThingServiceCallResult}
     */
    ThingServiceCallResult onThingServiceCall(String msgId, String identifier, Map<String, Object> inputData);
}
