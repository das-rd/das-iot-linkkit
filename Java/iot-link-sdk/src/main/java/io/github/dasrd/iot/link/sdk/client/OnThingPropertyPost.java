package io.github.dasrd.iot.link.sdk.client;

@FunctionalInterface
public interface OnThingPropertyPost {
    void onThingPropertyPost(String msgId,int code,Object data,String msg);
}
