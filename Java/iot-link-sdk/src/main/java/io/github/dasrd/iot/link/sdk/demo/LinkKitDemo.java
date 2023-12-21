package io.github.dasrd.iot.link.sdk.demo;

import io.github.dasrd.iot.link.sdk.client.*;
import io.github.dasrd.iot.link.sdk.request.PostResult;
import io.github.dasrd.iot.link.sdk.request.ThingPropertySetResult;
import io.github.dasrd.iot.link.sdk.request.ThingServiceCallResult;
import io.github.dasrd.iot.link.sdk.utils.MapBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static io.github.dasrd.iot.link.sdk.constants.Constants.SUCCESS_CODE;

/**
 * LinkKitDemo
 * linkKit可以看使用程序模拟设备
 *
 * @author tangsq
 * @date 2023/12/20
 */
@Slf4j
public class LinkKitDemo {
    /**
     * 设备id
     */
    private static final String IOT_DEVICE_ID = "56ghjyj";

    /**
     * 产品id
     */
    private static final String IOT_PRODUCT_ID = "7Xr3CCAN32Ske7Q8ZONfdSprR";

    /**
     * 设备秘钥
     */
    private static final String IOT_DEVICE_SECRET = "R5twi11141661627673604097";

    /**
     * mqtt host
     */
    private static final String MQTT_HOST = "192.168.100.101";

    /**
     * port
     */
    private static final String MQTT_PORT = "30247";

    /**
     * 客户端id
     */
    private static final String CLIENT_ID = "123";


    public static void main(String[] args) {

        LinkKit linkKit = new LinkKit(new LinkKitInitParams(IOT_PRODUCT_ID, IOT_DEVICE_ID, IOT_DEVICE_SECRET, MQTT_HOST, MQTT_PORT, CLIENT_ID));
        //初始化mqtt链接
        if (linkKit.init() != 0) {
            return;
        }
        //属性消息发送到服务端，服务端响应消息处理函数
        OnThingPropertyPost onThingPropertyPost = (String msgId, int code, Object data, String msg) -> {
            //todo 自定义处理逻辑
            if (SUCCESS_CODE.equals(code)) {
                log.info("消息发送成功并正常响应");
            } else {
                log.info("消息发送成功响应错误:{}", msg);
            }
        };

        //事件消息发送到服务端响应消息处理函数
        OnThingEventPost onThingEventPost = (String msgId, int code, Object data, String msg) -> {
            //todo 自定义处理逻辑
            if (SUCCESS_CODE.equals(code)) {
                log.info("消息发送成功并正常响应");
            } else {
                log.info("消息发送成功响应错误:{}", msg);
            }
        };

        //收到属性设置消息处理函数
        OnThingPropertySet onThingPropertySet = (String msgId, Map<String, Object> propData) -> {
            //todo 自定义处理逻辑，并返回ThingPropertySetResult对象
            return ThingPropertySetResult.successThingPropertySetResult();
        };

        //收到服务下发处理函数
        OnThingServiceCall onThingServiceCall = (String msgId, String identifier, Map<String, Object> inputData) -> {
            //todo 自定义处理逻辑，并返回ThingServiceCallResult对象
            Map<String, Map<String, Object>> map = new HashMap<>(4);
            map.put("params", MapBuilder.builder().put("service_output_test", 2).build());
            return ThingServiceCallResult.successThingServiceCallResult(map);
        };

        //绑定到工具
        linkKit.setOnThingPropertyPost(onThingPropertyPost);
        linkKit.setOnThingEventPost(onThingEventPost);
        linkKit.setOnThingPropertySet(onThingPropertySet);
        linkKit.setOnThingServiceCall(onThingServiceCall);

        //上报属性
        PostResult postResult = linkKit.thingPropertyPost(MapBuilder.builder().put("power").putS("value", 1).putS("time", System.currentTimeMillis()).build());
        //上报事件
        linkKit.thingEventPost(MapBuilder.builder().put("value").putS("event_input_1", 1).build(), "event_test");
    }
}
