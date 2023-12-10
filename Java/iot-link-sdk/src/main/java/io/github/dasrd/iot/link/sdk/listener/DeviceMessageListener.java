package io.github.dasrd.iot.link.sdk.listener;


import io.github.dasrd.iot.link.sdk.request.DeviceMessage;


public interface DeviceMessageListener {

    /**
     * 处理平台下发的设备消息
     *
     * @param deviceMessage 设备消息内容
     */
    void onDeviceMessage(DeviceMessage deviceMessage);
}
