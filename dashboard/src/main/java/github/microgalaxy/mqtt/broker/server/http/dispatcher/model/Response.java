package github.microgalaxy.mqtt.broker.server.http.dispatcher.model;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class Response {
    protected final HttpHeaders header = new DefaultHttpHeaders();
    protected HttpResponseStatus status = HttpResponseStatus.OK;
    protected byte[] body;

    public void addHeader(CharSequence name, Object value) {
        header.add(name, value);
    }

    public HttpHeaders getHeader() {
        return header;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public void setStatus(HttpResponseStatus status) {
        this.status = status;
    }

    public byte[] getBody() {
        return body;
    }
}
