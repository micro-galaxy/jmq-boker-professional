package github.microgalaxy.mqtt.broker.server.http.dispatcher.model;

import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMethod;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpVersion;

import java.util.Map;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class Request {
    private final String path;
    private final HttpVersion httpVersion;
    private final RequestMethod type;
    private final HttpHeaders header;
    private final Map<String, Object> uriParams;
    private final Map<String, Object> bodyParams;

    public Request(String path,HttpVersion httpVersion,RequestMethod type, HttpHeaders header,
                    Map<String, Object> uriParams, Map<String, Object> bodyParams) {
        this.path = path;
        this.httpVersion = httpVersion;
        this.type = type;
        this.header = header;
        this.uriParams = uriParams;
        this.bodyParams = bodyParams;
    }

    public String getPath() {
        return path;
    }

    public HttpVersion getHttpVersion() {
        return httpVersion;
    }

    public RequestMethod getType() {
        return type;
    }

    public HttpHeaders getHeader() {
        return header;
    }

    public Map<String, Object> getUriParams() {
        return uriParams;
    }

    public Map<String, Object> getBodyParams() {
        return bodyParams;
    }
}
