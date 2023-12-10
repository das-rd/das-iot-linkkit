package io.github.dasrd.iot.link.sdk.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IotEventRequest {
    private String id;
    private String version = "1.0";
    private Map<String,Integer> sys;
    private String identifier;
    private Map<String,Object> params;
    private String method = "thing.event.up";

}
