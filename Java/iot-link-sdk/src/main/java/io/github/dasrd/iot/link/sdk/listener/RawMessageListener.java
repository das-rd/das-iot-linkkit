package io.github.dasrd.iot.link.sdk.listener;

import io.github.dasrd.iot.link.sdk.transport.RawMessage;

public interface RawMessageListener {
    /**
     * 收到消息通知
     *
     * @param message 原始消息
     */
    void onMessageReceived(RawMessage message);
}
