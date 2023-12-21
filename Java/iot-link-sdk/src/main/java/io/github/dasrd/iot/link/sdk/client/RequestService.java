package io.github.dasrd.iot.link.sdk.client;

import io.github.dasrd.iot.link.sdk.request.IotRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 请求管理器
 * @author yangyi
 */
public class RequestService {

    private static final Logger log = LoggerFactory.getLogger(RequestService.class);

    private final ConcurrentHashMap<String, IotRequest> waitRequests = new ConcurrentHashMap<>();

    private final LinkKit deviceClient;

    RequestService(LinkKit deviceClient) {
        this.deviceClient = deviceClient;
    }

}
