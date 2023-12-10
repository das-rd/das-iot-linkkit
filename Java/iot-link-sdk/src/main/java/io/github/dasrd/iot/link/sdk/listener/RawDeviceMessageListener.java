package io.github.dasrd.iot.link.sdk.listener;


import io.github.dasrd.iot.link.sdk.request.RawDeviceMessage;


public interface RawDeviceMessageListener {

    /**
     * 处理平台下发的设备消息
     *
     * @param rawDeviceMessage 原始设备消息内容，
     */
    void onRawDeviceMessage(RawDeviceMessage rawDeviceMessage);
}
