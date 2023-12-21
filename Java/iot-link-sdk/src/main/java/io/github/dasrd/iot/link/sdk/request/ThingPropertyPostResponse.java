package io.github.dasrd.iot.link.sdk.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * sys/{product_id}/{device_id}/thing/properties/up/response
 * 数据格式
 * <p>
 * {
 * "code": 200,
 * "data": {},
 * "id": "123",
 * "message": "success",
 * "method": "thing.properties.up",
 * "version": "1.0"
 * }
 * <p>
 * {
 * "code": 460,
 * "data": {},
 * "id": "123",
 * "message": "request parameter error!",
 * "method": "thing.properties.up",
 * "version": "1.0"
 * }
 *
 * @author tangsq
 * @date 2023/12/19
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingPropertyPostResponse {
    /**
     * 结果状态码。 具体参考设备端通用code。
     * 说明 物联网平台会对设备上报的属性做校验。通过产品的TSL描述判断上报的属性是否符合定义的属性格式。不合格的属性会直接被过滤掉，并返回失败的错误码。
     */
    private Integer code;
    /**
     * 消息ID号，String类型的数字，取值范围0~4294967295，且每个消息ID在当前设备中具有唯一性。
     */
    private String id;
    /**
     * 返回结果信息。请求成功时，返回success。
     */
    private String message;
    /**
     * 协议版本号，与请求参数中version相同。
     */
    private String version;
    /**
     * 请求成功时，返回的数据固定为空。
     */
    private Map<String, Object> data;
    /**
     * 响应数据对应的请求方法，与请求参数中method相同。
     */
    private String method;
}
