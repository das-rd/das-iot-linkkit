package io.github.dasrd.iot.link.sdk.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tangsq
 */
public class MapBuilder {

    private Map<String, Object> map;

    private MapBuilder() {
        this.map = new HashMap<>(8);
    }

    public static MapBuilder builder() {
        return new MapBuilder();
    }

    public MapBuilder put(String key, Object value) {
        this.map.put(key, value);
        return this;
    }

    /**
     * 一级key设置
     *
     * @param key
     * @return {@link SecondLevelStringKeyValue}
     */
    public SecondLevelStringKeyValue put(String key) {
        Map<String, Object> secondLevel = new HashMap<>(16);
        SecondLevelStringKeyValue secondLevelStringKeyValue = new SecondLevelStringKeyValue(secondLevel);
        secondLevelStringKeyValue.setMapBuilder(this);
        this.map.put(key, secondLevel);
        return secondLevelStringKeyValue;
    }


    public Map<String, Object> build() {
        return map;
    }

    public static class SecondLevelStringKeyValue {


        private Map<String, Object> value;

        private MapBuilder mapBuilder;

        public SecondLevelStringKeyValue(Map<String, Object> value) {
            this.value = value;
        }

        private void setMapBuilder(MapBuilder mapBuilder) {
            this.mapBuilder = mapBuilder;
        }



        /**
         * 填充第二层数据
         *
         * @param key
         * @param value
         * @return {@link SecondLevelStringKeyValue}
         */
        public SecondLevelStringKeyValue putS(String key, Object value) {
            this.value.put(key, value);
            return this;
        }


        public Map<String, Object> build() {
            return this.mapBuilder.build();
        }
    }

    public static void main(String[] args) {
        Map<String, Object> map = MapBuilder.builder().put("power").putS("value", "on").putS("time", 1524448722000L).build();
        System.out.println(map.get("power"));
    }
}
