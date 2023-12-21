package io.github.dasrd.iot.link.sdk.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * sys/{product_id}/{device_id}/thing/service/set
 * 数据格式
 * <p>
 * {
 * "id": "123",
 * "version": "1.0",
 * "identifier": "powerService",
 * "params": {
 * "Power": "on",
 * "WF": "2"
 * },
 * "method": "thing.service.set"
 * }
 *
 * @author tangsq
 * @date 2023/12/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingServiceCallRequest {
    /**
     * 消息ID号，String类型的数字，取值范围0~4294967295，且每个消息ID在当前设备中具有唯一性。
     */
    private String id;
    /**
     * 协议版本号，目前协议版本号唯一取值为1.0。
     */
    private String version = "1.0";
    /**
     * 为物模型中定义的服务标识符，若为自定义模块，则为${tsl.functionBlockId}:${tsl.service.identifier}
     */
    private String identifier;
    /**
     * 服务调用参数。包含服务标识符和服务的值。如以上示例中的两个参数Power（电源）和WF（工作电流）。
     * {
     * "Power": "on",
     * "WF": "2"
     * }
     */
    private Map<String, Object> params;
    /**
     * 请求方法，取值为thing.service.set
     */
    private String method = "thing.service.set";
}
