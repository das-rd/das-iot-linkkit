package io.github.dasrd.iot.link.sdk.listener;


public interface RequestListener {
    /**
     * 请求执行完成通知
     *
     * @param result 请求执行结果
     */
    void onFinish(String result);
}
