package io.github.dasrd.iot.link.sdk.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThingEventPostResult {
    private String id;
    private String message;
    private String version;
    private Integer code;
    private Map<String,Object> data;
    private String method;
}
