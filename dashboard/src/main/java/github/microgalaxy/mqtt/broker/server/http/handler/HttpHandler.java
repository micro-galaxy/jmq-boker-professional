package github.microgalaxy.mqtt.broker.server.http.handler;

import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.Dispatcher;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.IDispatcher;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpMessage;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@ChannelHandler.Sharable
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpMessage> {
    private final Logger LOG = LoggerFactory.getLogger(this.getClass());
    private final IDispatcher dispatcher;
    private final BrokerProperties brokerProperties;
    private final HttpServerProperties properties;


    public HttpHandler(HttpServerProperties properties, BrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
        this.properties = properties;
        this.dispatcher = new Dispatcher(properties);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpMessage msg) throws Exception {
        HttpRequest httpRequest = HttpRequest.build(ctx, msg);
        HttpResponse httpResponse = HttpResponse.build(ctx, brokerProperties.getBrokerId());
        dispatcher.doRequest(httpRequest, httpResponse);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (!(evt instanceof IdleStateEvent)) {
            super.userEventTriggered(ctx, evt);
            return;
        }
        IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
        if (idleStateEvent.state() != IdleState.ALL_IDLE) return;
        ctx.channel().close();
        LOG.info("The server closed an connection, reason: The client keepalive timeout, IP:{}, ", ctx.channel().remoteAddress());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (!(cause instanceof IOException)) {
            super.exceptionCaught(ctx, cause);
            return;
        }
        ctx.channel().close();
        LOG.info("The client forcibly closed an existing connection:{} ,IP:{}",
                cause.getMessage(), ctx.channel().remoteAddress(), cause);
    }
}
