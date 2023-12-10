package io.github.dasrd.iot.link.sdk.transport;

import io.github.dasrd.iot.link.sdk.listener.BehaviorListener;


public interface Connection {

    int connect();

    /**
     * 发布消息
     *
     * @param message  消息
     * @param listener 发布监听器
     */
    void publishMessage(RawMessage message, BehaviorListener listener);

    /**
     * 关闭连接
     */
    void close();


    boolean isConnected();


    /**
     * @param behaviorListener 监听订阅是否成功
     * @param qos            qos
     */
    void subscribeTopic(String topic,BehaviorListener behaviorListener,int qos);

    void unsubscribeTopic(String topic,BehaviorListener listener);

}
