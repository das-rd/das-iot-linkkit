package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.client.handler.MessageReceivedHandler;
import io.github.dasrd.iot.link.sdk.listener.BehaviorListener;
import io.github.dasrd.iot.link.sdk.listener.DeviceMessageListener;
import io.github.dasrd.iot.link.sdk.listener.RawDeviceMessageListener;
import io.github.dasrd.iot.link.sdk.listener.RawMessageListener;
import io.github.dasrd.iot.link.sdk.mqtt.MqttConnection;
import io.github.dasrd.iot.link.sdk.request.*;
import io.github.dasrd.iot.link.sdk.transport.Connection;
import io.github.dasrd.iot.link.sdk.transport.RawMessage;
import io.github.dasrd.iot.link.sdk.utils.IotUtil;
import io.github.dasrd.iot.link.sdk.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.*;


/**
 * 连接工具类
 *
 * @author yangyi
 */
public class LinkKit implements RawMessageListener {
    private static final Logger log = LoggerFactory.getLogger(LinkKit.class);

    /**
     * 客户端线程数
     */
    private static final int CLIENT_THREAD_COUNT = 1;


    /**
     * mqtt连接异常错误码：用户名或密码错误
     */
    private static final int MQTT_EXCEPTION_OF_BAD_USERNAME_OR_PWD = 4;

    /**
     * 连接成功code
     */
    private static final int MQTT_CONNECT_SUCCESS = 0;


    /**
     * 线程池
     */
    private ScheduledExecutorService executorService;

    /**
     * 配置类
     */
    private final LinkKitInitParams clientConf;


    /**
     * mqtt连接
     */
    protected Connection connection;


    /**
     * topic->消息处理监听器
     */
    private final Map<String, RawMessageListener> rawMessageListenerMap;


    /**
     * 属性消息发送到服务端，服务端响应消息处理函数
     */
    private OnThingPropertyPost onThingPropertyPost;

    /**
     * 事件消息发送到服务端，服务端响应消息处理函数
     */
    private OnThingEventPost onThingEventPost;

    /**
     * 收到属性设置消息处理函数
     */
    private OnThingPropertySet onThingPropertySet;

    /**
     * 收到服务下发处理函数
     */
    private OnThingServiceCall onThingServiceCall;


    /**
     * topic->消息处理handler
     */
    private Map<String, MessageReceivedHandler> functionMap = new HashMap<>();


    /**
     * 好像没啥用?
     */
    private DeviceMessageListener deviceMessageListener;


    private RawDeviceMessageListener rawDeviceMessageListener;


    private final RequestService requestService;


    public LinkKit(LinkKitInitParams clientConf) {
        checkClientConf(clientConf);
        this.clientConf = clientConf;
        this.requestService = new RequestService(this);
        this.connection = new MqttConnection(clientConf, this);
        this.rawMessageListenerMap = new ConcurrentHashMap<>();

    }

    /**
     * 校验参数
     *
     * @param clientConf
     * @throws IllegalArgumentException
     */
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
     * 初始化，创建到平台的连接
     *
     * @return 如果连接成功，返回0；其它表示失败
     */
    public int init() {
        return connect();
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
        if (ret == MQTT_EXCEPTION_OF_BAD_USERNAME_OR_PWD) {
            return ret;
        }
        if (ret != MQTT_CONNECT_SUCCESS) {
            ret = IotUtil.reConnect(connection);
        }
        return ret;
    }

    /**
     * 关闭连接
     */
    public void close() {
        connection.close();
        if (null != executorService) {
            executorService.shutdown();
        }
    }


    /**
     * 发布原始消息，原始消息和设备消息（DeviceMessage）的区别是：
     * 1、可以自定义topic，该topic需要在平台侧配置
     * 2、不限制payload的格式
     *
     * @param rawMessage 原始消息
     * @param listener   监听器
     */
    public LinkKit publishTopic(RawMessage rawMessage, BehaviorListener listener) {
        connection.publishMessage(rawMessage, listener);
        return this;
    }


    /**
     * 收到消息回调
     *
     * @param message
     */
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
                Set<Map.Entry<String, MessageReceivedHandler>> entries = functionMap.entrySet();
                for (Map.Entry<String, MessageReceivedHandler> next : entries) {
                    if (topic.contains(next.getKey())) {
                        functionMap.get(next.getKey()).messageHandler(message);
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("onMessageReceived异常", e);
            }
        }, 0, TimeUnit.MILLISECONDS);
    }


    /**
     * 订阅自定义topic
     *
     * @param topic              自定义topic
     * @param actionListener     订阅结果监听器
     * @param rawMessageListener 接收自定义消息的监听器
     * @param qos                qos
     */
    public LinkKit subscribeTopic(String topic, BehaviorListener actionListener, RawMessageListener rawMessageListener, int qos) {
        connection.subscribeTopic(topic, actionListener, qos);
        rawMessageListenerMap.put(topic, rawMessageListener);
        return this;
    }

    /**
     * 取消订阅自定义topic
     *
     * @param topic          自定义topic
     * @param actionListener 订阅结果监听器
     */
    public void unsubscribeTopic(String topic, BehaviorListener actionListener) {
        connection.unsubscribeTopic(topic, actionListener);
    }

    /**
     * 属性上报
     * we send msg ===>  sys/{product_id}/{device_id}/thing/properties/up
     *
     * @param params
     * @return {@link PostResult}
     */
    public PostResult thingPropertyPost(Map<String, Object> params) {
        String topicProperties = "sys/" + this.clientConf.getProductId() + "/" + this.clientConf.getDeviceId() + "/thing/properties/up";
        IotPropertiesRequest iotPropertiesRequest = new IotPropertiesRequest();
        iotPropertiesRequest.setId(UUID.randomUUID().toString());
        HashMap<String, Integer> ack = new HashMap<>();
        ack.put("ack", 1);
        iotPropertiesRequest.setSys(ack);
        iotPropertiesRequest.setParams(params);
        PostResult postResult = new PostResult(iotPropertiesRequest.getId());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        this.publishTopic(new RawMessage(topicProperties, JsonUtils.toJson(iotPropertiesRequest)),
                new BehaviorListener() {
                    @Override
                    public void onSuccess(Object context) {
                        postResult.setSuccess(true);
                        countDownLatch.countDown();
                        log.info("[Publish Property Topic] ok");
                    }

                    @Override
                    public void onFailure(Object context, Throwable var2) {
                        postResult.setSuccess(false);
                        countDownLatch.countDown();
                        log.error("[Publish Property Topic] fail " + var2);
                    }
                });
        try {
            boolean timeout = countDownLatch.await(5, TimeUnit.SECONDS);
            if (timeout) {
                log.error("[Publish Property Topic] wait timeout");
            }
        } catch (Exception e) {
            log.error("[Publish Property Topic] error, wait result thread was be interrupted");
        }
        return postResult;
    }


    /**
     * 监听属性上报response消息
     * we received msg  <== sys/{product_id}/{device_id}/thing/properties/up/response
     */
    public LinkKit onThingPropertyPost() {
        String topicSetProperties = "sys/" + this.clientConf.getProductId() + "/" + this.clientConf.getDeviceId() + "/thing/properties/up/response";
        return this.subscribeTopic(topicSetProperties, new BehaviorListener() {
            @Override
            public void onSuccess(Object context) {
            }

            @Override
            public void onFailure(Object context, Throwable var2) {

            }
        }, message -> {
            String messageString = new String(message.getPayload());
            log.info("[Receive Property Response], data:{}", messageString);
            ThingPropertyPostResponse iotPropertiesResponse = JsonUtils.fromJson(messageString, ThingPropertyPostResponse.class);
            if (onThingPropertyPost != null) {
                onThingPropertyPost.onThingPropertyPost(iotPropertiesResponse.getId(), iotPropertiesResponse.getCode(), iotPropertiesResponse.getData(), iotPropertiesResponse.getMessage());
            }
        }, 0);
    }


    /**
     * 事件上报
     * we send msg ===>  sys/{product_id}/{device_id}/thing/event/up
     *
     * @param params
     * @param identifier
     * @return {@link PostResult}
     */
    public PostResult thingEventPost(Map<String, Object> params, String identifier) {
        String topicEvents = "sys/" + this.clientConf.getProductId() + "/" + this.clientConf.getDeviceId() + "/thing/event/up";
        IotEventRequest iotEventRequest = new IotEventRequest();
        iotEventRequest.setId(UUID.randomUUID().toString());
        HashMap<String, Integer> ack = new HashMap<>();
        ack.put("ack", 1);
        iotEventRequest.setSys(ack);
        iotEventRequest.setParams(params);
        iotEventRequest.setIdentifier(identifier);
        PostResult postResult = new PostResult(iotEventRequest.getId());
        CountDownLatch countDownLatch = new CountDownLatch(1);
        //发送事件消息
        publishTopic(new RawMessage(topicEvents, JsonUtils.toJson(iotEventRequest)),
                new BehaviorListener() {
                    @Override
                    //发送事件消息成功回调
                    public void onSuccess(Object context) {
                        postResult.setSuccess(true);
                        log.info("[Publish Event Topic] ok");
                    }

                    @Override
                    //发送事件消息失败回调
                    public void onFailure(Object context, Throwable var2) {
                        postResult.setSuccess(false);
                        log.info("[Publish Event Topic] fail ", var2);
                    }
                });
        try {
            boolean timeout = countDownLatch.await(5, TimeUnit.SECONDS);
            if (timeout) {
                log.error("[Publish Property Topic] wait timeout");
            }
        } catch (Exception e) {
            log.error("[Publish Property Topic] error, wait result thread was be interrupted");
        }
        return postResult;
    }

    /**
     * 监听事件上报response消息
     * we received msg  <== sys/{product_id}/{device_id}/thing/event/up/response
     *
     * @return {@link LinkKit}
     */
    public LinkKit onThingEventPost() {
        String topicSetProperties = "sys/" + this.clientConf.getProductId() + "/" + this.clientConf.getDeviceId() + "/thing/event/up/response";
        return this.subscribeTopic(topicSetProperties, new BehaviorListener() {
            @Override
            public void onSuccess(Object context) {
            }

            @Override
            public void onFailure(Object context, Throwable var2) {

            }
        }, message -> {
            String messageString = new String(message.getPayload());
            log.info("[Receive Event Response], data:{}", messageString);
            ThingEventPostResponse thingEventPostResponse = JsonUtils.fromJson(messageString, ThingEventPostResponse.class);
            if (onThingEventPost != null) {
                onThingEventPost.onThingEventPost(thingEventPostResponse.getId(), thingEventPostResponse.getCode(), thingEventPostResponse.getData(), thingEventPostResponse.getMessage());
            }
        }, 0);
    }


    /**
     * 监听属性设置消息并自定义回复
     * we received msg  <== sys/{product_id}/{device_id}/thing/properties/set
     */
    public LinkKit onThingPropertySet() {
        String topicSetProperties = "sys/" + this.clientConf.getProductId() + "/" + this.clientConf.getDeviceId() + "/thing/properties/set";
        return this.subscribeTopic(topicSetProperties, new BehaviorListener() {
            @Override
            public void onSuccess(Object context) {
            }

            @Override
            public void onFailure(Object context, Throwable var2) {

            }
        }, message -> {
            String messageString = new String(message.getPayload());
            log.info("[Receive Property Set], data:{}", messageString);
            IotPropertiesRequest iotPropertiesResponse = JsonUtils.fromJson(messageString, IotPropertiesRequest.class);
            if (onThingPropertySet != null) {
                ThingPropertySetResult thingPropertySetResult = onThingPropertySet.onThingPropertySet(iotPropertiesResponse.getId(), iotPropertiesResponse.getParams());
                thingPropertySetResult.fillDefaultInfo(iotPropertiesResponse);
                this.publishTopic(new RawMessage(topicSetProperties + "/response", JsonUtils.toJson(thingPropertySetResult)),
                        new BehaviorListener() {
                            @Override
                            public void onSuccess(Object context) {
                                log.info("response property set  ok: ");
                            }

                            @Override
                            public void onFailure(Object context, Throwable var2) {
                                log.error("response property set failed: " + var2);
                            }
                        });
            }
        }, 0);
    }


    /**
     * 监听云端服务调用并自定义回复
     * we received msg  <== sys/{product_id}/{device_id}/thing/service/set
     */
    public LinkKit onThingServiceCall() {
        String onThingServiceCallTopic = "sys/" + this.clientConf.getProductId() + "/" + this.clientConf.getDeviceId() + "/thing/service/set";
        return this.subscribeTopic(onThingServiceCallTopic, new BehaviorListener() {
            @Override
            public void onSuccess(Object context) {
            }

            @Override
            public void onFailure(Object context, Throwable var2) {

            }
        }, message -> {
            String messageString = new String(message.getPayload());
            log.info("[Receive Service Call], data:{}", messageString);
            ThingServiceCallRequest onThingServiceCallRequest = JsonUtils.fromJson(messageString, ThingServiceCallRequest.class);
            if (onThingServiceCall != null) {
                ThingServiceCallResult thingServiceCallResult = onThingServiceCall.onThingServiceCall(onThingServiceCallRequest.getId(), onThingServiceCallRequest.getIdentifier(), onThingServiceCallRequest.getParams());
                thingServiceCallResult.fillDefaultInfo(onThingServiceCallRequest);
                this.publishTopic(new RawMessage(onThingServiceCallTopic + "/response", JsonUtils.toJson(thingServiceCallResult)),
                        new BehaviorListener() {
                            @Override
                            public void onSuccess(Object context) {
                                log.info("response service call ok: ");
                            }

                            @Override
                            public void onFailure(Object context, Throwable var2) {
                                log.error("response service call fail: " + var2);
                            }
                        });
            }
        }, 0);
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

    /**
     * GET/SET函数
     *
     * @return {@link LinkKitInitParams}
     */
    public LinkKitInitParams getClientConf() {
        return clientConf;
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
     *
     * @param deviceMessageListener 消息监听器
     * @deprecated 该方法不能处理云端下发的任意格式的消息，请使用setRawDeviceMessageListener代替
     */
    @Deprecated
    public void setDeviceMessageListener(DeviceMessageListener deviceMessageListener) {
        this.deviceMessageListener = deviceMessageListener;
    }

    public OnThingPropertyPost getOnThingPropertyPost() {
        return onThingPropertyPost;
    }

    public LinkKit setOnThingPropertyPost(OnThingPropertyPost onThingPropertyPost) {
        this.onThingPropertyPost = onThingPropertyPost;
        this.onThingPropertyPost();
        return this;
    }

    public OnThingEventPost getOnThingEventPost() {
        return onThingEventPost;
    }

    public LinkKit setOnThingEventPost(OnThingEventPost onThingEventPost) {
        this.onThingEventPost = onThingEventPost;
        this.onThingEventPost();
        return this;
    }

    public OnThingPropertySet getOnThingPropertySet() {
        return onThingPropertySet;
    }

    public LinkKit setOnThingPropertySet(OnThingPropertySet onThingPropertySet) {
        this.onThingPropertySet = onThingPropertySet;
        this.onThingPropertySet();
        return this;
    }

    public OnThingServiceCall getOnThingServiceCall() {
        return onThingServiceCall;
    }

    public LinkKit setOnThingServiceCall(OnThingServiceCall onThingServiceCall) {
        this.onThingServiceCall = onThingServiceCall;
        this.onThingServiceCall();
        return this;
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


    public int getClientThreadCount() {
        return CLIENT_THREAD_COUNT;
    }

}
