package io.github.dasrd.iot.link.sdk.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingServiceCallResult {
    private String id;
    private String message;
    private String version = "1.0";
    private Integer code;
    private String identifier;
    private Map<String,Map<String,Object>> data;
    private String method = "thing.service.set";
}
