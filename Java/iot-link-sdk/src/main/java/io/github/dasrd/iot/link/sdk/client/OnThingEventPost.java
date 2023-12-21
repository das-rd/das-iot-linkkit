package io.github.dasrd.iot.link.sdk.client;

/**
 * 事件消息发送到服务端，服务端响应消息处理函数
 *
 * @author yangyi
 */
@FunctionalInterface
public interface OnThingEventPost {
    /**
     * 事件消息发送到服务端，服务端响应消息处理函数
     *
     * @param msgId
     * @param code
     * @param data
     * @param msg
     */
    void onThingEventPost(String msgId, int code, Object data, String msg);
}
