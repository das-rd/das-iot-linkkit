package io.github.dasrd.iot.link.sdk.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Map;

import static io.github.dasrd.iot.link.sdk.constants.Constants.SUCCESS;
import static io.github.dasrd.iot.link.sdk.constants.Constants.SUCCESS_CODE;

/**
 * sys/{product_id}/{device_id}/thing/service/set/response
 * 数据格式
 * {
 * "code": 200,
 * "identifier": "powerService",
 * "data": {
 * "params": {
 * "Power": "on",
 * "WF": "2"
 * }
 * },
 * "id": "123",
 * "message": "success",
 * "method": "thing.service.set",
 * "version": "1.0"
 * }
 * <p>
 * <p>
 * {
 * "code": 460,
 * "identifier": "powerService",
 * "data": {},
 * "id": "123",
 * "message": "request parameter error!",
 * "method": "thing.properties.set",
 * "version": "1.0"
 * }
 *
 * @author tangsq
 * @date 2023/12/20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingServiceCallResult {
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
     * 结果状态码。 具体参考设备端通用code。
     * 说明 物联网平台会对设备上报的属性做校验。通过产品的TSL描述判断上报的属性是否符合定义的属性格式。不合格的属性会直接被过滤掉，并返回失败的错误码。
     */
    private Integer code;
    /**
     * 为物模型中定义的服务标识符，若为自定义模块，则为${tsl.functionBlockId}:${tsl.service.identifier}
     */
    private String identifier;
    /**
     * 请求成功时，返回的数据固定为空。
     */
    private Map<String, Map<String, Object>> data;
    /**
     * 响应数据对应的请求方法，与请求参数中method相同
     */
    private String method;


    /**
     * 成功返回
     *
     * @return {@link ThingPropertySetResult}
     */
    public static ThingServiceCallResult successThingServiceCallResult(Map<String, Map<String, Object>> outputData) {
        ThingServiceCallResult thingPropertySetResult = new ThingServiceCallResult();
        thingPropertySetResult.setCode(SUCCESS_CODE);
        thingPropertySetResult.setMessage(SUCCESS);
        thingPropertySetResult.setData(outputData);
        return thingPropertySetResult;
    }

    /**
     * 失败返回
     * code != 200
     *
     * @return {@link ThingPropertySetResult}
     */
    public static ThingServiceCallResult failThingServiceCallResult(Integer code, String message, Map<String, Map<String, Object>> outputData) {
        ThingServiceCallResult thingPropertySetResult = new ThingServiceCallResult();
        thingPropertySetResult.setCode(code);
        thingPropertySetResult.setMessage(message);
        thingPropertySetResult.setData(outputData);
        return thingPropertySetResult;
    }

    /**
     * 填充默认值
     *
     * @param thingServiceCallRequest
     */
    public void fillDefaultInfo(ThingServiceCallRequest thingServiceCallRequest) {
        if (this.getId() == null || this.getId().length() == 0) {
            this.setId(thingServiceCallRequest.getId());
        }

        if (this.getIdentifier() == null || this.getIdentifier().length() == 0) {
            this.setIdentifier(thingServiceCallRequest.getIdentifier());
        }

        if (this.data == null) {
            this.setData(Collections.emptyMap());
        }

        if (this.getMethod() == null || this.getMethod().length() == 0) {
            this.setMethod(thingServiceCallRequest.getMethod());
        }

        if (this.getVersion() == null || this.getVersion().length() == 0) {
            this.setVersion(thingServiceCallRequest.getVersion());
        }
    }
}
