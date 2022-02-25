package github.microgalaxy.mqtt.broker.util;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.text.SimpleDateFormat;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public abstract class JsonUtils {
    private final static ObjectMapper JSON = new ObjectMapper();
    static {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JSON.setDateFormat(simpleDateFormat);
        JSON.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,false);
    }

    public static String beanToJson(Object bean) {
        try {
            return JSON.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to serialize objects", e);
        }
    }

    public static <T> T jsonToBean(String json, Class<T> clazz) {
        try {
            return JSON.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Unable to deserialize objects", e);
        }
    }

    public static <T> T beanToModel(Object bean, Class<T> clazz) {
        return JSON.convertValue(bean, clazz);
    }

}
