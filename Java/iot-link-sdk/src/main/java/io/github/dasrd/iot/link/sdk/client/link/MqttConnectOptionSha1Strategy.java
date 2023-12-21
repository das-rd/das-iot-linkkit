package io.github.dasrd.iot.link.sdk.client.link;

import io.github.dasrd.iot.link.sdk.client.LinkKitInitParams;
import io.github.dasrd.iot.link.sdk.constants.Constants;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * @author tangsq
 * @date 2023/12/19
 */
public class MqttConnectOptionSha1Strategy extends AbstractMqttConnectOptionsStrategy {
    public MqttConnectOptionSha1Strategy(LinkKitInitParams clientConf) {
        super(clientConf);
    }

    @Override
    public MqttConnectOptions getMqttConnectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setHttpsHostnameVerificationEnabled(false);
        options.setCleanSession(true);
        options.setUserName(clientConf.getDeviceId() + "&" + clientConf.getProductId());
        options.setPassword(getPassWordBySecret(clientConf).toCharArray());
        options.setConnectionTimeout(Constants.DEFAULT_CONNECT_TIMEOUT);
        options.setKeepAliveInterval(Constants.DEFAULT_KEEP_LIVE);
        options.setAutomaticReconnect(false);
        return options;
    }

    @Override
    public MqttAsyncClient getMqttAsyncClient() {
        String clientBuilderId = "{0}|securemode=2,signmethod=hmacsha1,timestamp={1}|";
        String clientId = MessageFormat.format(clientBuilderId, clientConf.getClientId(), clientConf.getTimestamp());
        try {
            return new MqttAsyncClient(clientConf.getServerUri(), clientId, new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        int i = Integer.parseInt("+90");
        System.out.println(i);
    }


    public String getPassWordBySecret(LinkKitInitParams clientConf) {
        Map<String, String> paramsDict = new HashMap<>();
        // MQTT连接参数
        String deviceId = clientConf.getDeviceId();
        String productId = clientConf.getProductId();
        String clientId = clientConf.getClientId();
        String deviceSecret = clientConf.getDeviceSecret();

        paramsDict.put("deviceId", deviceId);
        paramsDict.put("productId", productId);
        paramsDict.put("clientId", clientId);

        String plainPasswd = paramsDict.containsKey("clientId") ? "clientId" + paramsDict.get("clientId") : "clientId";
        plainPasswd += "deviceId" + paramsDict.get("deviceId") + "productId" + paramsDict.get("productId") +
                "timestamp" + clientConf.getTimestamp();


        String algorithm = "hmacsha1";
        String format = "%040x";

        String signResult = null;
        try {
            signResult = hmac(plainPasswd, deviceSecret, algorithm, format);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return signResult;
    }


    /**
     * HMAC加密
     *
     * @param plainText 明文
     * @param key       密钥
     * @param algorithm 算法
     * @param format    格式
     * @return 密文
     */
    private static String hmac(String plainText, String key, String algorithm, String format) throws Exception {
        if (plainText == null || key == null) {
            return null;
        }

        byte[] hmacResult = null;

        Mac mac = Mac.getInstance(algorithm);
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), algorithm);
        mac.init(secretKeySpec);
        hmacResult = mac.doFinal(plainText.getBytes());
        return String.format(format, new BigInteger(1, hmacResult));
    }
}
