package io.github.dasrd.iot.link.sdk.constants;

/**
 * @author yangyi
 */
public class Constants {
    /**
     * HMAC-SHA256不校验时间戳
     */
    public static final int CHECK_STAMP_SHA256_OFF = 0;

    /**
     * HMAC-SHA256校验时间戳
     */
    public static final int CHECK_STAMP_SHA256_ON = 1;

    /**
     * 连接超时时间
     */
    public static final int DEFAULT_CONNECT_TIMEOUT = 60;

    /**
     * 保活时间
     */
    public static final int DEFAULT_KEEP_LIVE = 120;



    /**
     * 成功code
     */
    public static  final Integer SUCCESS_CODE = 200;

    /**
     * success
     */
    public static  final String SUCCESS = "success";

}
