package io.github.dasrd.iot.link.sdk.client.link;

import io.github.dasrd.iot.link.sdk.client.LinkKitInitParams;

public abstract class AbstractMqttConnectOptionsStrategy implements MqttConnectOptionsStrategy {
    public LinkKitInitParams clientConf;

    public AbstractMqttConnectOptionsStrategy(LinkKitInitParams clientConf) {
        this.clientConf = clientConf;
    }

    public LinkKitInitParams getClientConf() {
        return clientConf;
    }

    public void setClientConf(LinkKitInitParams clientConf) {
        this.clientConf = clientConf;
    }
}
