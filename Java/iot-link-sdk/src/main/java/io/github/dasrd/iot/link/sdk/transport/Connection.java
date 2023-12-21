package io.github.dasrd.iot.link.sdk.transport;

import io.github.dasrd.iot.link.sdk.listener.BehaviorListener;
import org.eclipse.paho.client.mqttv3.MqttException;


/**
 * @author tangsq
 */
public interface Connection {

    /**
     * @return int
     */
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


    /**
     * @return boolean
     */
    boolean isConnected();


    /**
     * @param behaviorListener 监听订阅是否成功
     * @param qos              qos
     * @param topic
     */
    void subscribeTopic(String topic,BehaviorListener behaviorListener,int qos);

    /**
     * @param topic
     * @param listener
     */
    void unsubscribeTopic(String topic,BehaviorListener listener);

}
