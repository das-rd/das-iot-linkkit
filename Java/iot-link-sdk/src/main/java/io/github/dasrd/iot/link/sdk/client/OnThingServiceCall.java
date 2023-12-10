package io.github.dasrd.iot.link.sdk.client;

import java.util.Map;

@FunctionalInterface
public interface OnThingServiceCall {
    void onThingServiceCall(String msgId,String identifier,Map<String,Object> inputData);
}
