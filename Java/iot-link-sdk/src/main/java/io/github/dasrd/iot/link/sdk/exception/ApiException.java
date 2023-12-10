package io.github.dasrd.iot.link.sdk.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApiException extends RuntimeException {

    private Integer httpStatus;

    private Integer code;

    private String status;

    private String message;



    public ApiException(Integer httpStatus,Integer code,String status,String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.status = status;
        this.message = message;
    }

    /**
     * 默认使用httpStatus = 500以及code = 500
     *
     * @param status
     * @param message
     */
    public ApiException(String status,String message) {
        this.status = status;
        this.message = message;
    }


}
