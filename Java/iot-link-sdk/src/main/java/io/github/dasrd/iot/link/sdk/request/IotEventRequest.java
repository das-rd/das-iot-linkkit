package io.github.dasrd.iot.link.sdk.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * sys/{product_id}/{device_id}/thing/event/up
 * 数据格式
 * {
 * "id": "123",
 * "version": "1.0",
 * "sys":{
 * "ack":0
 * },
 * "identifier": "powerEvent",
 * "params": {
 * "value": {
 * "Power": "on",
 * "WF": "2"
 * },
 * "time": 1524448722000
 * },
 * "method": "thing.event.up"
 * }
 *
 * @author tangsq
 * @date 2023/12/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotEventRequest {
    /**
     * 消息ID号。String类型的数字，取值范围0~4294967295，且每个消息ID在当前设备中具有唯一性。
     */
    private String id;
    /**
     * 协议版本号，目前协议版本号唯一取值为1.0。
     */
    private String version = "1.0";
    /**
     * 扩展功能的参数，其下包含各功能字段。
     */
    private Map<String, Integer> sys;
    /**
     * 为物模型中定义的事件标识符，若为自定义模块，则为${tsl.functionBlockId}:${tsl.event.identifier}
     */
    private String identifier;
    /**
     * 上报事件的输出参数。
     */
    private Map<String, Object> params;
    /**
     * 请求方法。例如：thing.event.up
     */
    private String method = "thing.event.up";

}
