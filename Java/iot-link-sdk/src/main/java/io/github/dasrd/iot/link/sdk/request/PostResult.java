package io.github.dasrd.iot.link.sdk.request;


import lombok.Data;

/**
 * 事件发送结果
 *
 * @author tangsq
 * @date 2023/12/20
 */
@Data
public class PostResult {
    /**
     * 是否成功
     */
    private Boolean success;

    /**
     * 消息id
     */
    private String msgId;

    public PostResult(String msgId) {
        this.msgId = msgId;
        this.success = false;
    }
}
