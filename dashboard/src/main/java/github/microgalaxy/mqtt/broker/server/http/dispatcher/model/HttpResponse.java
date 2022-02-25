package github.microgalaxy.mqtt.broker.server.http.dispatcher.model;

import github.microgalaxy.mqtt.broker.util.JsonUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import io.netty.util.AsciiString;

import java.util.stream.IntStream;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class HttpResponse extends Response {
    private transient Channel channel;
    private Exception ex;

    private HttpResponse(Channel channel) {
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }

    public Exception getEx() {
        return ex;
    }

    public void setEx(Exception ex) {
        this.ex = ex;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static HttpResponse build(ChannelHandlerContext ctx, String serverId) {
        HttpResponse httpResponse = new HttpResponse(ctx.channel());
        httpResponse.addHeader(HttpHeaderNames.SERVER, "jmq");
        httpResponse.addHeader(HttpHeaderNames.SERVER.concat("-id"), serverId);
        return httpResponse;
    }

    public void writeJson(Object model) {
        header.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
        if (model instanceof CharSequence) {
            body = ((CharSequence) model).toString().getBytes();
            return;
        }
        body = JsonUtils.beanToJson(model).getBytes();
    }

    public void write(Byte[] fileBytes, AsciiString contentType) {
        header.add(HttpHeaderNames.CONTENT_TYPE, contentType);
        byte[] bytes = new byte[fileBytes.length];
        IntStream.range(0, fileBytes.length).forEach(i -> bytes[i] = fileBytes[i]);
        body = bytes;
    }

    public void write(String msg) {
        header.add(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.TEXT_PLAIN);
        body = msg.getBytes();
    }
}
