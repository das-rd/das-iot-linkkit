package io.github.dasrd.iot.link.sdk.client.link;

import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

public interface MqttConnectOptionsStrategy {

    MqttConnectOptions getMqttConnectOptions();
    MqttAsyncClient getMqttAsyncClient();
}
