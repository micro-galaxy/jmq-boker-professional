package github.microgalaxy.mqtt.broker.server.http.dispatcher.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class ResponseWrapper extends HashMap<String, Object> {

    private static final long serialVersionUID = 1L;

    public ResponseWrapper() {
        put("code", 0);
    }

    public static ResponseWrapper error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static ResponseWrapper error(String msg) {
        return error(500, msg);
    }

    public static ResponseWrapper error(int code, String msg) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        responseWrapper.put("code", code);
        responseWrapper.put("msg", msg);
        return responseWrapper;
    }

    public static ResponseWrapper ok(String msg) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        responseWrapper.put("msg", msg);
        return responseWrapper;
    }

    public static ResponseWrapper ok(Map<String, Object> map) {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        responseWrapper.putAll(map);
        return responseWrapper;
    }

    public static ResponseWrapper ok() {
        return new ResponseWrapper();
    }

    @Override
    public ResponseWrapper put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    public ResponseWrapper put(Object value) {
        super.put("data", value);
        return this;
    }
}