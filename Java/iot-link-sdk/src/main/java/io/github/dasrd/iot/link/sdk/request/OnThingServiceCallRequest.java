package io.github.dasrd.iot.link.sdk.request;

import lombok.Data;

import java.util.Map;

/**
 * @author yangyi
 * @version v 1.0
 * @date 2023/9/1 13:27
 */
@Data
public class OnThingServiceCallRequest {
    private String id;
    private String identifier;
    private Map<String,Object> params;
}
