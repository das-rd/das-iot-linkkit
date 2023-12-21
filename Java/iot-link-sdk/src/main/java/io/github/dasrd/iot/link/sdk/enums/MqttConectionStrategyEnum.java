package io.github.dasrd.iot.link.sdk.enums;

import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionDefaultStrategy;
import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionSha1Strategy;

public enum MqttConectionStrategyEnum {
    MQTT_CONNECT_OPTION_DEFAULT_STRATEGY("MqttConnectOptionDefaultStrategy", MqttConnectOptionDefaultStrategy.class),
    MQTT_CONNECT_OPTION_SHA1_STRATEGY("MqttConnectOptionSha1Strategy", MqttConnectOptionSha1Strategy.class),
    ;

    private String strategyName;

    private Class strategyClass;

    MqttConectionStrategyEnum(String strategyName,Class strategyClass) {
        this.strategyName = strategyName;
        this.strategyClass = strategyClass;
    }
}
