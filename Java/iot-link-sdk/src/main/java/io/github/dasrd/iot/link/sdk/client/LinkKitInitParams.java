package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionDefaultStrategy;
import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionSha1Strategy;
import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionsStrategy;
import io.github.dasrd.iot.link.sdk.enums.MqttConectionStrategyEnum;

/**
 * 客户端配置
 */
public class LinkKitInitParams implements Cloneable {
    /**
     * 产品id
     */
    private String productId;


    private String deviceId;

    /**
     * 设备秘钥
     */
    private String deviceSecret;

    private String mqttHost;

    private String mqttPort;


    private String clientId;

    /**
     * 协议类型，当前仅支持mqtt
     */
    private String protocol;


    /**
     * 客户端qos，0或1，默认1，仅MQTT协议支持
     */
    private int qos = 1;

    private MqttConectionStrategyEnum mqttConectionStrategyEnum;



    private String timestamp;


    public String getDeviceId() {
        return deviceId;
    }


    public String getServerUri() {
        return "tcp://" + getMqttHost() + ":" + getMqttPort();
    }


    public String getTimestamp() {
        if (this.timestamp == null) {
            this.timestamp = System.currentTimeMillis() + "";
        }
        return this.timestamp;
    }

    public MqttConectionStrategyEnum getMqttConectionStrategyEnum() {
        return mqttConectionStrategyEnum;
    }

    public void setMqttConectionStrategyEnum(MqttConectionStrategyEnum mqttConectionStrategyEnum) {
        this.mqttConectionStrategyEnum = mqttConectionStrategyEnum;
    }

    /**
     * 设置设备id
     *
     * @param deviceId 设备id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getDeviceSecret() {
        return deviceSecret;
    }

    public void setDeviceSecret(String deviceSecret) {
        this.deviceSecret = deviceSecret;
    }

    public String getMqttHost() {
        return mqttHost;
    }

    public void setMqttHost(String mqttHost) {
        this.mqttHost = mqttHost;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public int getQos() {
        return qos;
    }

    public void setQos(int qos) {
        this.qos = qos;
    }


    @Override
    public LinkKitInitParams clone() throws CloneNotSupportedException {
        return (LinkKitInitParams) super.clone();
    }


    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(String mqttPort) {
        this.mqttPort = mqttPort;
    }

    public MqttConnectOptionsStrategy getMqttConnectOptionsStrategy() {
        MqttConnectOptionsStrategy mqttConnectOptionsStrategy = null;
        if (MqttConectionStrategyEnum.Mqtt_Connect_Option_Default_Strategy.equals(this.getMqttConectionStrategyEnum())) {
            mqttConnectOptionsStrategy = new MqttConnectOptionDefaultStrategy(this);
        }
        if (MqttConectionStrategyEnum.Mqtt_Connect_Option_Sha1_Strategy.equals(this.getMqttConectionStrategyEnum())) {
            mqttConnectOptionsStrategy = new MqttConnectOptionSha1Strategy(this);
        }
        return mqttConnectOptionsStrategy;
    }
}
