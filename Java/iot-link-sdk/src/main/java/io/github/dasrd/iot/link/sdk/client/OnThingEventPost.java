package io.github.dasrd.iot.link.sdk.client;

@FunctionalInterface
public interface OnThingEventPost {
    void onThingEventPost(String msgId,int code,Object data,String msg);
}
