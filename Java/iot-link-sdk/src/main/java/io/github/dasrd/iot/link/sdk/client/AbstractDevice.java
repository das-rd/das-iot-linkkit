package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.enums.MqttConectionStrategyEnum;
import io.github.dasrd.iot.link.sdk.service.AbstractService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 抽象设备类
 *
 * @author yangyi
 */
public class AbstractDevice {
    private static final Logger log = LoggerFactory.getLogger(AbstractDevice.class);

    private final LinkKit client;

    private final String deviceId;

    private final String productId;

    private final Map<String, AbstractService> services = new ConcurrentHashMap<>();


    /**
     * 构造函数，创建设备
     */
    public AbstractDevice(String mqttHost, String mqttPort, String productId, String deviceId, String deviceSecret, String clientId, MqttConectionStrategyEnum mqttConectionStrategyEnum) {
        LinkKitInitParams clientConf = new LinkKitInitParams(productId, deviceId, deviceSecret, mqttHost, mqttPort, clientId);
        this.deviceId = deviceId;
        this.productId = productId;
        this.client = new LinkKit(clientConf);
        initSysServices();
        log.info("create device, the deviceId is {}", clientConf.getDeviceId());
    }


    /**
     * 初始化系统默认service，系统service以$作为开头
     */
    private void initSysServices() {
    }

    /**
     * 初始化，创建到平台的连接
     *
     * @return 如果连接成功，返回0；其它表示失败
     */
    public int init() {
        return client.connect();
    }

    /**
     * 添加服务。用户基于AbstractService定义自己的设备服务，并添加到设备
     *
     * @param serviceId     服务id，要和设备模型定义一致
     * @param deviceService 服务实例
     */
    public void addService(String serviceId, AbstractService deviceService) {

        deviceService.setIotDevice(this);
        deviceService.setServiceId(serviceId);

        services.putIfAbsent(serviceId, deviceService);
    }

    /**
     * 删除服务
     *
     * @param serviceId 服务id
     */
    public void delService(String serviceId) {
        services.remove(serviceId);
    }

    /**
     * 查询服务
     *
     * @param serviceId 服务id
     * @return AbstractService 服务实例
     */
    public AbstractService getService(String serviceId) {

        return services.get(serviceId);
    }


    /**
     * 获取设备客户端。获取到设备客户端后，可以直接调用客户端提供的消息、属性、命令等接口
     *
     * @return 设备客户端实例
     */
    public LinkKit getClient() {
        return client;
    }

    /**
     * 查询设备id
     *
     * @return 设备id
     */
    public String getDeviceId() {
        return deviceId;
    }

    public String getProductId() {
        return productId;
    }


}
