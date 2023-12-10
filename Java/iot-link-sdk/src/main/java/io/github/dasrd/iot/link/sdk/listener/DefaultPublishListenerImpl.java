package io.github.dasrd.iot.link.sdk.listener;

import io.github.dasrd.iot.link.sdk.transport.RawMessage;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultPublishListenerImpl implements IMqttActionListener {
    private static final Logger log = LoggerFactory.getLogger(DefaultPublishListenerImpl.class);

    private final BehaviorListener listener;
    private final RawMessage message;

    public DefaultPublishListenerImpl(BehaviorListener listener,
                                      RawMessage message) {
        this.listener = listener;
        this.message = message;
    }

    @Override
    public void onSuccess(IMqttToken iMqttToken) {
        if (listener != null) {
            listener.onSuccess(null);
        }
    }

    @Override
    public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
        log.error("publish message failed  " + message);
        if (listener != null) {
            listener.onFailure(null, throwable);
        }
    }
}
