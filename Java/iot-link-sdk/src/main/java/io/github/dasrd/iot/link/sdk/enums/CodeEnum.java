package io.github.dasrd.iot.link.sdk.enums;

import lombok.Getter;


@Getter
public enum CodeEnum {
    SUCCESS(200,"success"),
    FAIL(500,"fail"),
    ;
    private Integer code;
    private String msg;

    CodeEnum(Integer code,String msg) {
        this.code = code;
        this.msg = msg;
    }
}
