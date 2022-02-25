package github.microgalaxy.mqtt.broker.server.http.dispatcher.model;

import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMethod;
import github.microgalaxy.mqtt.broker.server.http.util.HttpUtils;
import github.microgalaxy.mqtt.broker.util.JsonUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class HttpRequest extends Request {
    private transient Channel channel;
    private Exception buildEx;

    private HttpRequest(String path, Channel channel, HttpVersion httpVersion, RequestMethod type, HttpHeaders header,
                        Map<String, Object> uriParams, Map<String, Object> bodyParams) {
        super(path, httpVersion, type, header, uriParams, bodyParams);
        this.channel = channel;
    }

    public Channel getChannel() {
        return channel;
    }


    public Exception getBuildEx() {
        return buildEx;
    }

    public void setBuildEx(Exception buildEx) {
        this.buildEx = buildEx;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public static HttpRequest build(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        FullHttpRequest request = (FullHttpRequest) msg;
        String uri = request.uri();
        RequestMethod type = RequestMethod.valueOf(request.method().name());
        Map<String, Object> uriParams = HttpUtils.getRequestUriParams(uri);
        Map<String, Object> bodyParams = new HashMap<>();
        if (RequestMethod.POST == type) {
            if (!request.headers().get(HttpHeaderNames.CONTENT_TYPE).startsWith(HttpHeaderValues.APPLICATION_JSON.toString())) {
                RuntimeException ex = new RuntimeException("Only json parameters are supported");
                HttpRequest httpRequestEx = new HttpRequest(HttpUtils.getRequestBaseUri(uri), ctx.channel(), request.protocolVersion(), type, request.headers(), uriParams, bodyParams);
                httpRequestEx.setBuildEx(ex);
                return httpRequestEx;
            }

            ByteBuf content = request.content();
            byte[] contentBytes = new byte[content.readableBytes()];
            content.getBytes(content.readerIndex(), contentBytes);
            String jsonStr = new String(contentBytes);
            if (!StringUtils.isEmpty(jsonStr)) bodyParams = JsonUtils.jsonToBean(jsonStr, HashMap.class);
        }
        return new HttpRequest(HttpUtils.getRequestBaseUri(uri), ctx.channel(), request.protocolVersion(), type, request.headers(), uriParams, bodyParams);
    }
}
