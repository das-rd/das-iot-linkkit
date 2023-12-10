package io.github.dasrd.iot.link.sdk.listener;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultSubscribeListenerImpl implements IMqttActionListener {
    private static final Logger log = LoggerFactory.getLogger(DefaultSubscribeListenerImpl.class);

    private final String topic;

    private final BehaviorListener listener;

    public DefaultSubscribeListenerImpl(String topic,BehaviorListener listener) {
        this.topic = topic;
        this.listener = listener;
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        if (listener != null) {
            listener.onSuccess(topic);
        }
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        log.error("subscribe topic failed:" + topic);
        if (listener != null) {
            listener.onFailure(topic, throwable);
        }
    }
}
