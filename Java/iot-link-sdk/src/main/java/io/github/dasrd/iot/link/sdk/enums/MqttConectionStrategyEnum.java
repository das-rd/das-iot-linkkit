package io.github.dasrd.iot.link.sdk.enums;

import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionDefaultStrategy;
import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionSha1Strategy;

public enum MqttConectionStrategyEnum {
    Mqtt_Connect_Option_Default_Strategy("MqttConnectOptionDefaultStrategy", MqttConnectOptionDefaultStrategy.class),
    Mqtt_Connect_Option_Sha1_Strategy("MqttConnectOptionSha1Strategy", MqttConnectOptionSha1Strategy.class),
    ;

    private String strategyName;

    private Class strategyClass;

    MqttConectionStrategyEnum(String strategyName,Class strategyClass) {
        this.strategyName = strategyName;
        this.strategyClass = strategyClass;
    }
}
