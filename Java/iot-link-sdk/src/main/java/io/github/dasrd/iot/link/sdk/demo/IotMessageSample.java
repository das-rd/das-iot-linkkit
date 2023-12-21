//package io.github.dasrd.iot.link.sdk.demo;
//
//import com.das.iot.sdk.device.client.Device;
//import com.das.iot.sdk.device.enums.MqttConectionStrategyEnum;
//import com.das.iot.sdk.device.request.*;
//import lombok.extern.slf4j.Slf4j;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//@Slf4j
//public class IotMessageSample {
//    //设备id
//    private static final String IOT_DEVICE_ID = "56ghjyj";
//    //产品id
//    private static final String IOT_PRODUCT_ID = "7Xr3CCAN32Ske7Q8ZONfdSprR";
//    //设备秘钥
//    private static final String IOT_DEVICE_SECRET = "R5twi11141661627673604097";
//
//    public static void main(String[] args) throws InterruptedException, IOException {
//        // 创建设备
//        Device device = new Device("192.168.100.26","30394",IOT_PRODUCT_ID,IOT_DEVICE_ID,
//                IOT_DEVICE_SECRET,"12345",MqttConectionStrategyEnum.Mqtt_Connect_Option_Sha1_Strategy);
//        device.getClient().setOnThingEventPost((String msgId,int code,Object data,String msg)->{
//            log.info("事件上报：msgId:{},code:{},data:{},msg:{}",msgId,code,data,msg);
//        });
//        device.getClient().setOnThingPropertyPost((String msgId,int code,Object data,String msg)->{
//            log.info("属性上报：msgId:{},code:{},data:{},msg:{}",msgId,code,data,msg);
//        });
//        device.getClient().setOnThingPropertySet((String msgId,int code,Object data,String msg)->{
//            log.info("云端属性设置：msgId:{},code:{},data:{},msg:{}",msgId,code,data,msg);
//        });
//        device.getClient().setOnThingServiceCall((String msgId,String identifier,Map<String,Object> inputData)->{
//            log.info("云端服务调用：msgId:{},identifier:{},inputData:{}",msgId,identifier,inputData);
//        });
//        //初始化mqtt链接
//        if (device.init() != 0) {
//            return;
//        }
//
//        // 接收平台下行消息
//        while (true) {
//            //接受事件消息
//            onThingEventPostByLinkKit(device);
//            Thread.sleep(5000);
//        }
//    }
//
//
//    private static void onThingEventPostByLinkKit(Device device) {
//        Map<String,Object> params = new HashMap<>();
//        Map<String,Object> value = new HashMap<>();
//        value.put("event_input_1",1);
//        params.put("time",System.currentTimeMillis());
//        params.put("value",value);
//        device.getClient().thingEventPost(params,"event_test");
//    }
//
//    private static void onThingPropertyPostByLinkKit(Device device) {
//        Map<String,Object> params = new HashMap<>();
//        Map<String,Object> power = new HashMap<>();
//        power.put("value",1);
//        power.put("time",System.currentTimeMillis());
//        params.put("power",power);
//        device.getClient().thingPropertyPost(params);
//    }
//
//
//
//
//
//}
