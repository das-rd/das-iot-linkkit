package io.github.dasrd.iot.link.sdk.client.handler;


import io.github.dasrd.iot.link.sdk.transport.RawMessage;

public interface MessageReceivedHandler {
    void messageHandler(RawMessage message);
}
