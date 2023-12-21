package io.github.dasrd.iot.link.sdk.mqtt;

import io.github.dasrd.iot.link.sdk.client.LinkKitInitParams;
import io.github.dasrd.iot.link.sdk.client.link.MqttConnectOptionsStrategy;
import io.github.dasrd.iot.link.sdk.constants.Constants;
import io.github.dasrd.iot.link.sdk.listener.BehaviorListener;
import io.github.dasrd.iot.link.sdk.listener.DefaultPublishListenerImpl;
import io.github.dasrd.iot.link.sdk.listener.DefaultSubscribeListenerImpl;
import io.github.dasrd.iot.link.sdk.listener.RawMessageListener;
import io.github.dasrd.iot.link.sdk.transport.ConnectListener;
import io.github.dasrd.iot.link.sdk.transport.Connection;
import io.github.dasrd.iot.link.sdk.transport.RawMessage;
import io.github.dasrd.iot.link.sdk.utils.IotUtil;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.internal.ConnectActionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MqttConnection implements Connection {
    private static final Logger log = LoggerFactory.getLogger(MqttConnection.class);

    private static final int DEFAULT_QOS = 1;


    private static final String CONNECT_TYPE_OF_DEVICE = "0";

    private static final String CONNECT_TYPE_OF_BRIDGE_DEVICE = "3";

    private static final int CONNECT_OF_BRIDGE_MODE = 3;


    private final LinkKitInitParams clientConf;

    private boolean connectFinished = false;

    private MqttAsyncClient mqttAsyncClient;

    private ConnectListener connectListener;

    private ConnectActionListener connectActionListener;

    private RawMessageListener rawMessageListener;

    private int connectResultCode;

    public MqttConnection(LinkKitInitParams clientConf, RawMessageListener rawMessageListener) {
        this.clientConf = clientConf;
        this.rawMessageListener = rawMessageListener;
    }

    private final MqttCallback callback = new MqttCallbackExtended() {

        @Override
        public void connectionLost(Throwable cause) {
            log.error("Connection lost.", cause);
            if (connectListener != null) {
                connectListener.connectionLost(cause);
            }

            IotUtil.reConnect(MqttConnection.this);
        }

        @Override
        public void messageArrived(String topic, MqttMessage message) {
            log.info("messageArrived topic =  {}, msg = {}", topic, message.toString());
            RawMessage rawMessage = new RawMessage(topic, message.toString());
            try {
                if (rawMessageListener != null) {
                    rawMessageListener.onMessageReceived(rawMessage);
                }

            } catch (Exception e) {
                log.error("messageArrived异常", e);
            }

        }

        @Override
        public void deliveryComplete(IMqttDeliveryToken token) {

        }

        @Override
        public void connectComplete(boolean reconnect, String serverURI) {
            log.info("Mqtt client connected. address is {}", serverURI);

            if (connectListener != null) {
                connectListener.connectComplete(reconnect, serverURI);
            }
        }
    };

    @Override
    public int connect() {

        try {
            this.connectFinished = false;
            MqttConnectOptionsStrategy mqttConnectOptionsStrategy = clientConf.getMqttConnectOptionsStrategy();
            mqttAsyncClient = mqttConnectOptionsStrategy.getMqttAsyncClient();

            DisconnectedBufferOptions bufferOptions = new DisconnectedBufferOptions();
            bufferOptions.setBufferEnabled(true);
            if (createMqttConnection(bufferOptions, mqttConnectOptionsStrategy)) {
                return -1;
            }

            synchronized (this) {
                while (!connectFinished) {
                    try {
                        wait(Constants.DEFAULT_CONNECT_TIMEOUT * 1000);
                    } catch (InterruptedException e) {
                        log.error("InterruptedException异常", e);
                    }
                }
            }

        } catch (MqttException e) {
            log.error("connect error, the deviceId is {}. exception is {}", clientConf.getDeviceId(),
                    e);
        }

        if (mqttAsyncClient.isConnected()) {
            return 0;
        }

        // 处理paho返回的错误码为0的异常
        if (connectResultCode == 0) {
            log.error("Client encountered an exception");
            return -1;
        }

        return connectResultCode;
    }

    private boolean createMqttConnection(DisconnectedBufferOptions bufferOptions, MqttConnectOptionsStrategy mqttConnectOptionsStrategy)
            throws MqttException {
        mqttAsyncClient.setBufferOpts(bufferOptions);

        mqttAsyncClient.setCallback(callback);

        mqttAsyncClient.connect(mqttConnectOptionsStrategy.getMqttConnectOptions(), null, getCallback());
        return false;
    }


    private IMqttActionListener getCallback() {
        return new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {

                log.info("connect success, the uri is {}", clientConf.getServerUri());

                if (connectActionListener != null) {
                    connectActionListener.onSuccess(iMqttToken);
                }

                synchronized (MqttConnection.this) {
                    connectFinished = true;
                    MqttConnection.this.notifyAll();
                }
            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                log.info("connect failed, the reason is {}", throwable.toString());
                if (throwable instanceof MqttException) {
                    MqttException me = (MqttException) throwable;
                    connectResultCode = me.getReasonCode();
                }

                if (connectActionListener != null) {
                    connectActionListener.onFailure(iMqttToken, throwable);
                }

                synchronized (MqttConnection.this) {
                    connectFinished = true;
                    MqttConnection.this.notifyAll();
                }
            }
        };
    }


    @Override
    public void publishMessage(RawMessage message, BehaviorListener listener) {

        try {
            MqttMessage mqttMessage = new MqttMessage(message.getPayload());
            mqttMessage.setQos(message.getQos() == 0 ? 0 : DEFAULT_QOS);

            DefaultPublishListenerImpl defaultPublishListener = new DefaultPublishListenerImpl(listener, message);

            mqttAsyncClient.publish(message.getTopic(), mqttMessage, message.getTopic(), defaultPublishListener);
            log.info("publish message topic is {}, msg =  {}", message.getTopic(), message.toString());
        } catch (MqttException e) {
            log.error("publishMessage异常", e);
            if (listener != null) {
                listener.onFailure(null, e);
            }
        }
    }

    @Override
    public void close() {

        if (mqttAsyncClient.isConnected()) {
            try {
                mqttAsyncClient.disconnect();
            } catch (MqttException e) {
                log.error("close异常", e);
            }
        }
    }

    @Override
    public boolean isConnected() {
        if (mqttAsyncClient == null) {
            return false;
        }
        return mqttAsyncClient.isConnected();
    }


    /**
     * 订阅指定主题
     *
     * @param topic 主题
     */
    @Override
    public void subscribeTopic(String topic, BehaviorListener listener, int qos) {
        DefaultSubscribeListenerImpl defaultSubscribeListener = new DefaultSubscribeListenerImpl(topic, listener);
        try {
            mqttAsyncClient.subscribe(topic, qos, null, defaultSubscribeListener);
        } catch (MqttException e) {
            log.error("subscribeTopic异常", e);
            if (listener != null) {
                listener.onFailure(topic, e);
            }
        }

    }

    /**
     * 取消订阅指定主题
     *
     * @param topic 主题
     */
    @Override
    public void unsubscribeTopic(String topic, BehaviorListener listener) {
        DefaultSubscribeListenerImpl defaultSubscribeListener = new DefaultSubscribeListenerImpl(topic, listener);
        try {
            mqttAsyncClient.unsubscribe(topic, null, defaultSubscribeListener);
        } catch (MqttException e) {
            log.error("unsubscribeTopic异常", e);
            if (listener != null) {
                listener.onFailure(topic, e);
            }
        }

    }


}
