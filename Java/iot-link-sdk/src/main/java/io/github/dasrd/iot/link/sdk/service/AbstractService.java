package io.github.dasrd.iot.link.sdk.service;

import io.github.dasrd.iot.link.sdk.client.AbstractDevice;
import com.fasterxml.jackson.annotation.JsonFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * 设备服务抽象类
 */
@JsonFilter("AbstractService")
public abstract class AbstractService{
    private static final Logger log = LoggerFactory.getLogger(AbstractService.class);

    private AbstractDevice iotDevice;

    private final Map<String, Method> commands = new HashMap<>();

    private final Map<String, Field> writeableFields = new HashMap<>();

    private final Map<String, FieldPair> readableFields = new HashMap<>();

    private Timer timer;

    private String serviceId;

    private static class FieldPair {
        String propertyName;

        Field field;

        FieldPair(String propertyName, Field field) {
            this.propertyName = propertyName;
            this.field = field;
        }
    }

    public AbstractService() {
        for (Field field : this.getClass().getDeclaredFields()) {

            Property property = field.getAnnotation(Property.class);
            if (property == null) {
                continue;
            }

            String name = property.name();
            if (name.isEmpty()) {
                name = field.getName();
            }
            if (property.writeable()) {
                writeableFields.put(name, field);
            }

            // 这里key是字段名,pair里保存属性名
            readableFields.put(field.getName(), new FieldPair(name, field));
        }

        for (Method method : this.getClass().getDeclaredMethods()) {
            DeviceCommand deviceCommand = method.getAnnotation(DeviceCommand.class);
            if (deviceCommand == null) {
                continue;
            }
            String name = deviceCommand.name();
            if (name.isEmpty()) {
                name = method.getName();
            }
            commands.put(name, method);
        }
    }

    private Object getFiledValue(String fieldName) {
        Field field = readableFields.get(fieldName).field;
        if (field == null) {
            log.error("field is null: " + fieldName);
            return null;
        }
        String getter = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);
        Method method;

        try {
            method = this.getClass().getDeclaredMethod(getter);
        } catch (NoSuchMethodException e) {
            return null;
        }

        if (method == null) {
            log.error("method is null: " + getter);
            return null;
        }

        try {
            return method.invoke(this);
        } catch (IllegalAccessException | InvocationTargetException e) {
        }

        return null;
    }




    /**
     * 获取设备实例
     *
     * @return 设备实例
     */
    protected AbstractDevice getIotDevice() {
        return iotDevice;
    }

    /**
     * 设置设备实例
     *
     * @param iotDevice 设备实例
     */
    public void setIotDevice(AbstractDevice iotDevice) {
        this.iotDevice = iotDevice;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }


    /**
     * 关闭自动周期上报，您可以通过firePropertiesChanged触发上报
     */
    public void disableAutoReport() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
}