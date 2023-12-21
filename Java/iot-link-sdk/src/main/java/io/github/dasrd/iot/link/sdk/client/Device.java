package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.enums.MqttConectionStrategyEnum;
import io.github.dasrd.iot.link.sdk.service.AbstractService;


public class Device extends AbstractDevice {

    /**
     * 构造函数，使用密码创建设备
     *
     * @param mqttPort     端口
     * @param deviceId     设备id
     * @param deviceSecret 设备密码
     * @param clientId     客户端id
     */
    public Device(String mqttHost,String mqttPort,String productId,String deviceId,String deviceSecret,String clientId,MqttConectionStrategyEnum mqttConectionStrategyEnum) {
        super(mqttHost, mqttPort,productId,deviceId, deviceSecret, clientId,mqttConectionStrategyEnum);

    }



    /**
     * 初始化，创建到平台的连接
     *
     * @return 此接口为阻塞调用，如果连接成功，返回0；否则返回-1
     */
    @Override
    public int init() {
        return super.init();
    }

    /**
     * 添加服务。用户基于AbstractService定义自己的设备服务，并添加到设备
     *
     * @param serviceId     服务id，要和设备模型定义一致
     * @param deviceService 服务实例
     */
    @Override
    public void addService(String serviceId, AbstractService deviceService) {
        super.addService(serviceId, deviceService);
    }


    @Override
    public AbstractService getService(String serviceId) {
        return super.getService(serviceId);
    }





    /**
     * 获取直连设备客户端。获取到设备客户端后，可以直接调用客户端提供的消息、属性、命令等接口
     *
     * @return 设备客户端实例
     */
    @Override
    public LinkKit getClient() {
        return super.getClient();
    }
}
