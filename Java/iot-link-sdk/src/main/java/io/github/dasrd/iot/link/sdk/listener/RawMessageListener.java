package io.github.dasrd.iot.link.sdk.listener;

import io.github.dasrd.iot.link.sdk.transport.RawMessage;

/**
 * 消息监听接口
 *
 * @author tangsq
 * @date 2023/12/19
 */
public interface RawMessageListener {
    /**
     * 收到消息通知
     *
     * @param message 原始消息
     */
    void onMessageReceived(RawMessage message);
}
