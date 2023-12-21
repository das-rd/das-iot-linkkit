package io.github.dasrd.iot.link.sdk.client.link;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

/**
 * @author tangsq
 */
public interface MqttConnectOptionsStrategy {

    /**
     * @return {@link MqttConnectOptions}
     */
    MqttConnectOptions getMqttConnectOptions();

    /**
     * @return {@link MqttAsyncClient}
     */
    MqttAsyncClient getMqttAsyncClient();
}
