package github.microgalaxy.mqtt.broker.server.http.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Configuration
@ConfigurationProperties(prefix = "http.server")
public class HttpServerProperties implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private final List<Consumer<Class<?>>> onBeanReadyEvents = new ArrayList<>();

    private Integer port = 80;

    /**
     * default 1M
     */
    private Integer maxContentLength = 1024 * 1024;

    /**
     * default 60s
     */
    private Integer keepAlive = 60;


    /**
     * Sokcet参数, 存放已完成三次握手请求的队列最大长度
     */
    private int soBacklog = 512;

    /**
     * Socket参数, 是否开启心跳保活机制, 默认开启
     */
    private boolean soKeepAlive = true;

    /**
     * classPath: WEB-INF; => resources/WEB-INF
     * /opt/xxx/web; => Server absolute directory
     */
    private String resourcePath = "classpath: webapp";

    /**
     * 是否开启Epoll模式, 默认关闭
     */
    private boolean useEpoll = false;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getMaxContentLength() {
        return maxContentLength;
    }

    public Integer getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(Integer keepAlive) {
        this.keepAlive = keepAlive;
    }

    public int getSoBacklog() {
        return soBacklog;
    }

    public void setSoBacklog(int soBacklog) {
        this.soBacklog = soBacklog;
    }

    public boolean isSoKeepAlive() {
        return soKeepAlive;
    }

    public void setSoKeepAlive(boolean soKeepAlive) {
        this.soKeepAlive = soKeepAlive;
    }

    public void setMaxContentLength(Integer maxContentLength) {
        this.maxContentLength = maxContentLength;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(String resourcePath) {
        this.resourcePath = resourcePath;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        HttpServerProperties.applicationContext = applicationContext;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public void addOnBeanReadyEvent(Consumer<Class<?>> onBeanReadyEvent) {
        this.onBeanReadyEvents.add(onBeanReadyEvent);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onInitBeanDone() {
        onBeanReadyEvents.forEach(v -> v.accept(ApplicationReadyEvent.class));
    }
}
