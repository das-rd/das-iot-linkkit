package io.github.dasrd.iot.link.sdk.client;

/**
 * 属性消息发送到服务端，服务端响应消息处理函数
 *
 * @author tangsq
 */
@FunctionalInterface
public interface OnThingPropertyPost {
    /**
     * 属性消息发送到服务端，服务端响应消息处理函数
     *
     * @param msgId
     * @param code
     * @param data
     * @param msg
     */
    void onThingPropertyPost(String msgId, int code, Object data, String msg);
}
