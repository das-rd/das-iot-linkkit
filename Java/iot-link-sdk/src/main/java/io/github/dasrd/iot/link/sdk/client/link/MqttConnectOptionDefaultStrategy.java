package io.github.dasrd.iot.link.sdk.client.link;

import io.github.dasrd.iot.link.sdk.client.LinkKitInitParams;
import io.github.dasrd.iot.link.sdk.constants.Constants;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttConnectOptionDefaultStrategy extends AbstractMqttConnectOptionsStrategy{
    public MqttConnectOptionDefaultStrategy(LinkKitInitParams clientConf) {
        super(clientConf);
    }

    @Override
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setHttpsHostnameVerificationEnabled(false);
        options.setCleanSession(true);
        options.setUserName(clientConf.getDeviceId());
        options.setConnectionTimeout(Constants.DEFAULT_CONNECT_TIMEOUT);
        options.setKeepAliveInterval(Constants.DEFAULT_KEEPLIVE);
        options.setAutomaticReconnect(false);
        return options;
    }

    @Override
    public MqttAsyncClient getMqttAsyncClient() {
        try {
            return new MqttAsyncClient(clientConf.getServerUri(), clientConf.getClientId(), new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return null;
    }

}
