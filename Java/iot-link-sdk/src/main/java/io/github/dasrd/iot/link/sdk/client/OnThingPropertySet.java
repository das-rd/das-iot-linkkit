package io.github.dasrd.iot.link.sdk.client;

@FunctionalInterface
public interface OnThingPropertySet {
    void onThingPropertySet(String msgId,int code,Object data,String msg);
}
