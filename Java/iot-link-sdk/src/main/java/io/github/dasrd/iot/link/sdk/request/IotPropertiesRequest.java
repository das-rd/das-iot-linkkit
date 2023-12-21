package io.github.dasrd.iot.link.sdk.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * sys/{product_id}/{device_id}/thing/properties/up
 * sys/{product_id}/{device_id}/thing/properties/set
 * 数据格式
 * {
 * "id": "123",
 * "version": "1.0",
 * "sys":{
 * "ack":0
 * },
 * "params": {
 * "Power": {
 * "value": "on",
 * "time": 1524448722000
 * },
 * "WF": {
 * "value": 23.6,
 * "time": 1524448722000
 * }
 * },
 * "method": "thing.properties.up"
 * }
 *
 * @author tangsq
 * @date 2023/12/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotPropertiesRequest {
    /**
     * 消息id
     */
    private String id;


    /**
     * 协议版本号，目前协议版本号唯一取值为1.0。
     */
    private String version = "1.0";

    /**
     * 扩展功能的参数，其下包含各功能字段。
     * 说明 使用设备端SDK开发时，如果未设置扩展功能，则无此参数，相关功能保持默认配置。
     */
    private Map<String, Integer> sys;

    /**
     * 请求参数。如以上示例中，设备上报了的两个属性Power（电源）和WF（工作电流）的信息。具体属性信息，包含属性上报时间（time）和上报的属性值（value）。
     * 若仅传入属性值，无需上传字段time和value，params示例如下：
     * <p>
     * "params": {
     * "Power": "on",
     * "WF": 23.6
     * }
     * 如果是自定义模块属性，属性标识符格式为${模块标识符}:${属性标识符}（中间为英文冒号）。例如：
     * <p>
     * "test:Power": {
     * "value": "on",
     * "time": 1524448722000
     * }
     */
    private Map<String, Object> params;

    /**
     * 请求方法。例如：thing.properties.up
     */
    private String method = "thing.properties.up";
}
