package io.github.dasrd.iot.link.sdk.client.handler;

import io.github.dasrd.iot.link.sdk.client.LinkKit;
import io.github.dasrd.iot.link.sdk.request.DeviceMessage;
import io.github.dasrd.iot.link.sdk.request.RawDeviceMessage;
import io.github.dasrd.iot.link.sdk.transport.RawMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageHandler implements MessageReceivedHandler {
    private static final Logger log = LoggerFactory.getLogger(MessageHandler.class);

    private final LinkKit deviceClient;

    public MessageHandler(LinkKit deviceClient) {
        this.deviceClient = deviceClient;
    }

    @Override
    public void messageHandler(RawMessage message) {
        RawDeviceMessage rawDeviceMessage = new RawDeviceMessage(message.getPayload());
        DeviceMessage deviceMessage = rawDeviceMessage.toDeviceMessage();
        boolean isSystemFormat = (deviceMessage != null);

        if (deviceClient.getRawDeviceMessageListener() != null) {
            log.debug("receive message in custom format:  {}", message);
            deviceClient.getRawDeviceMessageListener().onRawDeviceMessage(rawDeviceMessage);
        }

        if (isSystemFormat) {
            log.debug("receive message in system format: {}", message);
            boolean isCurrentDevice = (deviceMessage.getDeviceId() == null
                    || deviceMessage.getDeviceId().equals(deviceClient.getDeviceId()));

            if (deviceClient.getDeviceMessageListener() != null && isCurrentDevice) {
                deviceClient.getDeviceMessageListener().onDeviceMessage(deviceMessage);
                return;
            }
        }
    }
}
