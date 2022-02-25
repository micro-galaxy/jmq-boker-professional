package github.microgalaxy.mqtt.broker.server.http;

import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.server.MqttServer;
import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.server.http.handler.HttpHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Service
public class HttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(MqttServer.class);

    private BrokerProperties brokerProperties;
    private HttpServerProperties serverProperties;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    private EventLoopGroup businessGroup;
    private Channel httpChannel;

    @Autowired
    private void setBrokerProperties(BrokerProperties brokerProperties) {
        this.brokerProperties = brokerProperties;
    }
    @Autowired
    private void setServerProperties(HttpServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @PostConstruct
    public void start() throws Exception {
        LOGGER.info("Starting http Server...");
        bossGroup = serverProperties.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        workerGroup = serverProperties.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        businessGroup = serverProperties.isUseEpoll() ? new EpollEventLoopGroup() : new NioEventLoopGroup();
        httpServer();
        shutdownHook();
        LOGGER.info("Http server is running,  port:{}", serverProperties.getPort());
    }

    private void httpServer() throws Exception {
        ServerBootstrap server = new ServerBootstrap();
        HttpHandler httpHandler = new HttpHandler(serverProperties,brokerProperties);
        server.group(bossGroup, workerGroup)
                .channel(serverProperties.isUseEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addFirst("idleStateHandler", new IdleStateHandler(0, 0, serverProperties.getKeepAlive()))
                                .addLast("httpCodec", new HttpServerCodec())
                                .addLast("aggregator", new HttpObjectAggregator(serverProperties.getMaxContentLength()))
                                .addLast(businessGroup, "httpHandler", httpHandler);
                    }
                })
                .option(ChannelOption.SO_BACKLOG, serverProperties.getSoBacklog())
                .childOption(ChannelOption.SO_KEEPALIVE, serverProperties.isSoKeepAlive());
        httpChannel = server.bind(serverProperties.getPort()).sync().channel();
    }

    private void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("Http server is shutdown...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
            httpChannel.close();
        }));
    }
}
