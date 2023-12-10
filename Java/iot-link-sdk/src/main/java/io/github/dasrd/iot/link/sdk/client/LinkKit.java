package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.client.handler.MessageReceivedHandler;
import io.github.dasrd.iot.link.sdk.request.*;
import io.github.dasrd.iot.link.sdk.listener.BehaviorListener;
import io.github.dasrd.iot.link.sdk.listener.DeviceMessageListener;
import io.github.dasrd.iot.link.sdk.listener.RawDeviceMessageListener;
import io.github.dasrd.iot.link.sdk.listener.RawMessageListener;
import io.github.dasrd.iot.link.sdk.mqtt.MqttConnection;
import io.github.dasrd.iot.link.sdk.transport.Connection;
import io.github.dasrd.iot.link.sdk.transport.RawMessage;
import io.github.dasrd.iot.link.sdk.utils.IotUtil;
import io.github.dasrd.iot.link.sdk.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.*;


public class LinkKit implements RawMessageListener {
    private static final Logger log = LoggerFactory.getLogger(LinkKit.class);

    private static final int CLIENT_THREAD_COUNT = 1;


    private static final int MQTTEXCEPTION_OF_BAD_USERNAME_OR_PWD = 4;

    private static final int MQTT_CONNECT_SUCCESS = 0;

    private DeviceMessageListener deviceMessageListener;

    private RawDeviceMessageListener rawDeviceMessageListener;

    private LinkKitInitParams clientConf;

    protected Connection connection;

    private RequestService requestService;

    private String deviceId;

    private Map<String, RawMessageListener> rawMessageListenerMap;

    private AbstractDevice device;

    private ScheduledExecutorService executorService;

    Map<String, MessageReceivedHandler> functionMap = new HashMap<>();

    private OnThingPropertyPost onThingPropertyPost;

    private OnThingEventPost onThingEventPost;

    private OnThingPropertySet onThingPropertySet;

    private OnThingServiceCall onThingServiceCall;

    public OnThingServiceCall getOnThingServiceCall() {
        return onThingServiceCall;
    }

    public void setOnThingServiceCall(OnThingServiceCall onThingServiceCall) {
        this.onThingServiceCall = onThingServiceCall;
    }

    public OnThingPropertySet getOnThingPropertySet() {
        return onThingPropertySet;
    }

    public void setOnThingPropertySet(OnThingPropertySet onThingPropertySet) {
        this.onThingPropertySet = onThingPropertySet;
    }

    public OnThingEventPost getOnThingEventPost() {
        return onThingEventPost;
    }

    public void setOnThingEventPost(OnThingEventPost onThingEventPost) {
        this.onThingEventPost = onThingEventPost;
    }

    public LinkKit() {
    }


    public LinkKit(LinkKitInitParams clientConf,AbstractDevice device) {

        checkClientConf(clientConf);
        this.clientConf = clientConf;
        this.deviceId = clientConf.getDeviceId();
        this.requestService = new RequestService(this);
        this.connection = new MqttConnection(clientConf, this);
        this.device = device;
        this.rawMessageListenerMap = new ConcurrentHashMap<>();

    }

    public LinkKitInitParams getClientConf() {
        return clientConf;
    }

    public OnThingPropertyPost getOnThingPropertyPost() {
        return onThingPropertyPost;
    }

    public void setOnThingPropertyPost(OnThingPropertyPost onThingPropertyPost) {
        this.onThingPropertyPost = onThingPropertyPost;
    }

    private void checkClientConf(LinkKitInitParams clientConf) throws IllegalArgumentException {
        if (clientConf == null) {
            throw new IllegalArgumentException("clientConf is null");
        }
        if (clientConf.getDeviceId() == null) {
            throw new IllegalArgumentException("clientConf.getDeviceId() is null");
        }
        if (clientConf.getServerUri() == null) {
            throw new IllegalArgumentException("clientConf.getSecret() is null");
        }
        if (!clientConf.getServerUri().startsWith("tcp://")) {
            throw new IllegalArgumentException("invalid serverUri");
        }
    }

    /**
     * 和平台建立连接，此接口为阻塞调用
     *
     * @return 0表示连接成功，其他表示连接失败
     */
    public int connect() {

        synchronized (this) {
            if (executorService == null) {
                executorService = Executors.newScheduledThreadPool(CLIENT_THREAD_COUNT);
            }
        }

        int ret = connection.connect();

        // 如果是userName或password填写错误，则不重连
        if (ret == MQTTEXCEPTION_OF_BAD_USERNAME_OR_PWD) {
            return ret;
        }

        if (ret != MQTT_CONNECT_SUCCESS) {
            ret = IotUtil.reConnect(connection);
        }

        return ret;
    }





    /**
     * 发布原始消息，原始消息和设备消息（DeviceMessage）的区别是：
     * 1、可以自定义topic，该topic需要在平台侧配置
     * 2、不限制payload的格式
     *
     * @param rawMessage 原始消息
     * @param listener   监听器
     */
    public LinkKit publishTopic(RawMessage rawMessage,BehaviorListener listener) {
        connection.publishMessage(rawMessage, listener);
        return this;
    }




    @Override
    public void onMessageReceived(RawMessage message) {

        if (executorService == null) {
            log.error("executionService is null");
            return;
        }

        executorService.schedule(() -> {
            try {
                String topic = message.getTopic();

                RawMessageListener listener = rawMessageListenerMap.get(topic);
                if (listener != null) {
                    listener.onMessageReceived(message);
                    return;
                }

                Set<Map.Entry<String,MessageReceivedHandler>> entries = functionMap.entrySet();
                Iterator<Map.Entry<String, MessageReceivedHandler>> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, MessageReceivedHandler> next = iterator.next();
                    if (topic.contains(next.getKey())) {
                        functionMap.get(next.getKey()).messageHandler(message);
                        break;
                    }
                }

            } catch (Exception e) {
                log.error("onMessageReceived异常",e);
            }
        }, 0, TimeUnit.MILLISECONDS);

    }

    public void close() {
        connection.close();
        if (null != executorService) {
            executorService.shutdown();
        }
    }





    /**
     * 获取设备id
     *
     * @return 设备id
     */
    public String getDeviceId() {
        return deviceId;
    }




    /**
     * 订阅自定义topic
     *
     * @param topic              自定义topic
     * @param actionListener     订阅结果监听器
     * @param rawMessageListener 接收自定义消息的监听器
     * @param qos                qos
     */
    public void subscribeTopic(String topic,BehaviorListener actionListener,RawMessageListener rawMessageListener,
                               int qos) {
        connection.subscribeTopic(topic, actionListener, qos);
        rawMessageListenerMap.put(topic, rawMessageListener);
    }

    /**
     * 取消订阅自定义topic
     *
     * @param topic              自定义topic
     * @param actionListener     订阅结果监听器
     */
    public void unsubscribeTopic(String topic,BehaviorListener actionListener) {
        connection.unsubscribeTopic(topic, actionListener);
    }



    public AbstractDevice getDevice() {
        return device;
    }

    public DeviceMessageListener getDeviceMessageListener() {
        return deviceMessageListener;
    }

    public RawDeviceMessageListener getRawDeviceMessageListener() {
        return rawDeviceMessageListener;
    }

    public RequestService getRequestService() {
        return requestService;
    }



    /**
     * 设置消息监听器，用于接收平台下发的消息
     * 此监听器只能接收平台到直连设备的请求，子设备的请求由AbstractGateway处理
     * @param deviceMessageListener 消息监听器
     * @deprecated 该方法不能处理云端下发的任意格式的消息，请使用setRawDeviceMessageListener代替
     */
    @Deprecated
    public void setDeviceMessageListener(DeviceMessageListener deviceMessageListener) {
        this.deviceMessageListener = deviceMessageListener;
    }

    /**
     * 设置消息监听器，用于接收平台下发的消息
     * 此监听器只能接收平台到直连设备的请求，子设备的请求由AbstractGateway处理
     *
     * @param rawDeviceMessageListener 消息监听器
     */
    public void setRawDeviceMessageListener(RawDeviceMessageListener rawDeviceMessageListener) {
        this.rawDeviceMessageListener = rawDeviceMessageListener;
    }

    /**
     * 获取各类topic处理的handler
     *
     * @return 各类topic处理的handler
     */
    public Map<String, MessageReceivedHandler> getFunctionMap() {
        return functionMap;
    }

    /**
     * 设置各类topic处理的handler
     *
     * @param functionMap 各类topic处理的handler
     */
    public void setFunctionMap(
        Map<String, MessageReceivedHandler> functionMap) {
        this.functionMap = functionMap;
    }

    public void setDevice(AbstractDevice device) {
        this.device = device;
    }


    public Future<?> scheduleTask(Runnable runnable) {
        return executorService.schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    public Future<?> scheduleTask(Runnable runnable, long delay) {
        return executorService.schedule(runnable, delay, TimeUnit.MILLISECONDS);
    }

    public Future<?> scheduleRoutineTask(Runnable runnable, long period) {
        return executorService.scheduleAtFixedRate(runnable, period, period, TimeUnit.MILLISECONDS);
    }

    public int getClientThreadCount() {
        return CLIENT_THREAD_COUNT;
    }

    public void onThingServiceCall() {
        String onThingServiceCallTopic = "sys/" + device.getProductId() + "/" + device.getDeviceId() + "/thing/service/set";
        device.getClient().subscribeTopic(onThingServiceCallTopic,new BehaviorListener() {
            @Override
            public void onSuccess(Object context) {
            }

            @Override
            public void onFailure(Object context,Throwable var2) {

            }
        },new RawMessageListener() {
            @Override
            public void onMessageReceived(RawMessage message) {
                String messageString = new String(message.getPayload());

                OnThingServiceCallRequest onThingServiceCallRequest = JsonUtils.fromJson(messageString,OnThingServiceCallRequest.class);

                if (onThingServiceCall != null) {
                    onThingServiceCall.onThingServiceCall(onThingServiceCallRequest.getId(),onThingServiceCallRequest.getIdentifier(),onThingServiceCallRequest.getParams()
                    );
                }
            }
        },0);
    }

    public void onThingPropertySet() {
        String topicSetProperties = "sys/" + device.getProductId() + "/" + device.getDeviceId() + "/thing/properties/set";
        device.getClient().subscribeTopic(topicSetProperties,new BehaviorListener() {
            @Override
            public void onSuccess(Object context) {
            }

            @Override
            public void onFailure(Object context,Throwable var2) {

            }
        },new RawMessageListener() {
            @Override
            public void onMessageReceived(RawMessage message) {
                String messageString = new String(message.getPayload());

                ThingPropertyPostResult iotPropertiesResponse = JsonUtils.fromJson(messageString,ThingPropertyPostResult.class);

                if (onThingPropertySet != null) {
                    onThingPropertySet.onThingPropertySet(iotPropertiesResponse.getId(),iotPropertiesResponse.getCode()
                            ,iotPropertiesResponse.getData(),iotPropertiesResponse.getMessage()
                    );
                }
            }
        },0);
    }



    public void thingPropertyPost(Map<String,Object> params) {
        String topicProperties = "sys/" + device.getProductId() + "/" + device.getDeviceId() + "/thing/properties/up";
        IotPropertiesRequest iotPropertiesRequest = new IotPropertiesRequest();
        iotPropertiesRequest.setId(UUID.randomUUID().toString());
        HashMap<String,Integer> ack = new HashMap<>();
        ack.put("ack",1);
        iotPropertiesRequest.setSys(ack);
        iotPropertiesRequest.setParams(params);
        device.getClient().publishTopic(new RawMessage(topicProperties,JsonUtils.toJson(iotPropertiesRequest)),
                new BehaviorListener() {
                    @Override
                    public void onSuccess(Object context) {
                        log.info("publishTopic ok: ");
                    }

                    @Override
                    public void onFailure(Object context,Throwable var2) {
                        log.error("publishTopic fail: " + var2);
                    }
                }).subscribeTopic(topicProperties + "/response",new BehaviorListener() {
            @Override
            public void onSuccess(Object context) {
            }

            @Override
            public void onFailure(Object context,Throwable var2) {

            }
        },new RawMessageListener() {
            @Override
            public void onMessageReceived(RawMessage message) {
                String messageString = new String(message.getPayload());

                ThingPropertyPostResult iotPropertiesResponse = JsonUtils.fromJson(messageString,ThingPropertyPostResult.class);

                if (onThingPropertyPost != null) {
                    onThingPropertyPost.onThingPropertyPost(iotPropertiesResponse.getId(),iotPropertiesResponse.getCode()
                            ,iotPropertiesResponse.getData(),iotPropertiesResponse.getMessage()
                    );
                }
                System.out.println("properties-response:" + messageString);
            }
        },0);
    }


    //设备事件消息
    public void thingEventPost(Map<String,Object> params,String identifier) {
        String topicEvents = "sys/" + device.getProductId() + "/" + device.getDeviceId() + "/thing/event/up";
        IotEventRequest iotEventRequest = new IotEventRequest();
        iotEventRequest.setId(UUID.randomUUID().toString());
        HashMap<String,Integer> ack = new HashMap<>();
        ack.put("ack",1);
        iotEventRequest.setSys(ack);
        iotEventRequest.setParams(params);
        iotEventRequest.setIdentifier(identifier);
        //发送事件消息
        device.getClient().publishTopic(new RawMessage(topicEvents,JsonUtils.toJson(iotEventRequest)),
                        new BehaviorListener() {
                            @Override
                            //发送事件消息成功回调
                            public void onSuccess(Object context) {
                                log.info("publishTopic ok: ");
                            }

                            @Override
                            //发送事件消息失败回调
                            public void onFailure(Object context,Throwable var2) {
                                log.error("publishTopic fail: " + var2);
                            }
                        })
                //监听事件消息响应
                .subscribeTopic(topicEvents + "/response",new BehaviorListener() {
                    //监听事件消息成功回调
                    @Override
                    public void onSuccess(Object context) {
                    }

                    @Override
                    //监听事件消息失败回调
                    public void onFailure(Object context,Throwable var2) {

                    }
                    //事件消息响应
                },new RawMessageListener() {
                    @Override
                    public void onMessageReceived(RawMessage message) {

                        String s = new String(message.getPayload());

                        ThingEventPostResult iotEventResponse = JsonUtils.fromJson(s,ThingEventPostResult.class);

                        if (onThingEventPost != null) {
                            onThingEventPost.onThingEventPost(iotEventResponse.getId(),iotEventResponse.getCode()
                                    ,iotEventResponse.getData(),iotEventResponse.getMessage()
                            );
                        }
                    }
                },0);
    }


}
