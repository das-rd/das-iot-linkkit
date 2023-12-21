package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionDefaultStrategy;
import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionSha1Strategy;
import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionsStrategy;
import io.github.dasrd.iot.link.sdk.enums.MqttConectionStrategyEnum;
import lombok.Getter;

import static io.github.dasrd.iot.link.sdk.enums.MqttConectionStrategyEnum.MQTT_CONNECT_OPTION_SHA1_STRATEGY;

/**
 * 客户端配置
 *
 * @author tangsq
 */
@Getter
public class LinkKitInitParams implements Cloneable {
    /**
     * 产品id
     */
    private final String productId;

    /**
     * 设备id
     */
    private final String deviceId;

    /**
     * 设备秘钥
     */
    private final String deviceSecret;

    /**
     * mqtt服务器地址
     */
    private final String mqttHost;

    /**
     * mqtt服务器端口
     */
    private final String mqttPort;

    /**
     * 客户端id
     */
    private final String clientId;

    /**
     * 协议类型，当前仅支持mqtt
     */
    private final String protocol;

    /**
     * 客户端qos，0或1，默认1，仅MQTT协议支持
     */
    private final Integer qos;

    /**
     * 默认sha1加密
     */
    private final MqttConectionStrategyEnum mqttConectionStrategyEnum;


    /**
     * 时间戳
     */
    private final String timestamp;


    /**
     * @param productId
     * @param deviceId
     * @param deviceSecret
     * @param mqttHost
     * @param mqttPort
     */
    public LinkKitInitParams(String productId, String deviceId, String deviceSecret, String mqttHost, String mqttPort) {
        this.productId = productId;
        this.deviceId = deviceId;
        this.deviceSecret = deviceSecret;
        this.mqttHost = mqttHost;
        this.mqttPort = mqttPort;
        this.clientId = null;
        this.protocol = null;
        this.qos = 1;
        this.mqttConectionStrategyEnum = MQTT_CONNECT_OPTION_SHA1_STRATEGY;
        this.timestamp = String.valueOf(System.currentTimeMillis());

    }

    /**
     *
     *
     * @param productId
     * @param deviceId
     * @param deviceSecret
     * @param mqttHost
     * @param mqttPort
     * @param clientId
     */
    public LinkKitInitParams(String productId, String deviceId, String deviceSecret, String mqttHost, String mqttPort, String clientId) {
        this.productId = productId;
        this.deviceId = deviceId;
        this.deviceSecret = deviceSecret;
        this.mqttHost = mqttHost;
        this.mqttPort = mqttPort;
        this.clientId = clientId;
        this.protocol = null;
        this.qos = 1;
        this.mqttConectionStrategyEnum = MQTT_CONNECT_OPTION_SHA1_STRATEGY;
        this.timestamp = String.valueOf(System.currentTimeMillis());
    }


    public String getServerUri() {
        return "tcp://" + getMqttHost() + ":" + getMqttPort();
    }


    public MqttConnectOptionsStrategy getMqttConnectOptionsStrategy() {
        MqttConnectOptionsStrategy mqttConnectOptionsStrategy = null;
        if (MqttConectionStrategyEnum.MQTT_CONNECT_OPTION_DEFAULT_STRATEGY.equals(this.mqttConectionStrategyEnum)) {
            mqttConnectOptionsStrategy = new MqttConnectOptionDefaultStrategy(this);
        }
        if (MQTT_CONNECT_OPTION_SHA1_STRATEGY.equals(this.mqttConectionStrategyEnum)) {
            mqttConnectOptionsStrategy = new MqttConnectOptionSha1Strategy(this);
        }
        return mqttConnectOptionsStrategy;
    }

    @Override
    public LinkKitInitParams clone() throws CloneNotSupportedException {
        return (LinkKitInitParams) super.clone();
    }
}
