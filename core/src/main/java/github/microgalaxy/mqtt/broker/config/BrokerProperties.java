package github.microgalaxy.mqtt.broker.config;

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
 * 服务配置
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Configuration
@ConfigurationProperties("jmq.broker")
public class BrokerProperties implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    private final List<Consumer<Class<?>>> onBeanReadyEvents = new ArrayList<>();

    /**
     * 节点id
     */
    private String brokerId;
    /**
     * mqtt默认端口：1883
     */
    private int mqttPort = 1883;
    /**
     * mqtt ssl默认端口：8883
     */
    private int mqttSslPort = 8883;

    /**
     * websocket 默认端口：8083
     */
    private int mqttWsPort = 8083;
    /**
     * websocket ssl默认端口：8084
     */
    private int mqttWssPort = 8084;
    /**
     * websocket path默认值：/mqtt
     */
    private String wsPath = "/mqtt";
    /**
     * CA 证书目录相对目录，项目目录下
     */
    private String caCertificateDir = "ssl";

    /**
     * 是否开启Epoll模式, 默认关闭
     */
    private boolean useEpoll = false;

    /**
     * 心跳检测时间(秒), 默认值60秒； 客户端连接时可指定
     *
     * @link https://docs.oasis-open.org/mqtt/mqtt/v5.0/os/mqtt-v5.0-os.html#_Toc3901045
     */
    private int keepAlive = 60;

    /**
     * Sokcet参数, 存放已完成三次握手请求的队列最大长度
     */
    private int soBacklog = 512;

    /**
     * Socket参数, 是否开启心跳保活机制, 默认开启
     */
    private boolean soKeepAlive = true;

    /**
     * TCP参数, 是否禁用Nagle算法，默认禁用
     */
    private boolean tcpNoDelay = true;

    /**
     * MqttDecoder参数, MqttDecoder 单次发送数据大小，默认：8092 bytes
     */
    private int payloadLength = 8092;

    /**
     * 集群配置, 是否基于组播发现, 默认开启
     * <p>
     * 默认spi发现服务端口：47500-47509
     * 数据交换端口：47100-47109
     */
    private boolean enableMulticastGroup = true;

    /**
     * 集群配置, 基于组播发现
     */
    private String clusterMulticastGroupIp = "239.192.0.1";

    /**
     * 集群认证密钥
     */
    private String clusterKey;

    /**
     * 集群配置, 当组播模式禁用时, 使用静态IP开启配置集群
     */
    private String[] clusterStaticIps = new String[]{};

    /**
     * Jmq版本信息
     */
    private String version;

    public String getBrokerId() {
        return brokerId;
    }

    public void setBrokerId(String brokerId) {
        this.brokerId = brokerId;
    }

    public int getMqttPort() {
        return mqttPort;
    }

    public void setMqttPort(int mqttPort) {
        this.mqttPort = mqttPort;
    }

    public int getMqttSslPort() {
        return mqttSslPort;
    }

    public void setMqttSslPort(int mqttSslPort) {
        this.mqttSslPort = mqttSslPort;
    }

    public int getMqttWsPort() {
        return mqttWsPort;
    }

    public void setMqttWsPort(int mqttWsPort) {
        this.mqttWsPort = mqttWsPort;
    }

    public int getMqttWssPort() {
        return mqttWssPort;
    }

    public void setMqttWssPort(int mqttWssPort) {
        this.mqttWssPort = mqttWssPort;
    }

    public String getWsPath() {
        return wsPath;
    }

    public void setWsPath(String wsPath) {
        this.wsPath = wsPath;
    }

    public String getCaCertificateDir() {
        return caCertificateDir;
    }

    public void setCaCertificateDir(String caCertificateDir) {
        this.caCertificateDir = caCertificateDir;
    }

    public boolean isUseEpoll() {
        return useEpoll;
    }

    public void setUseEpoll(boolean useEpoll) {
        this.useEpoll = useEpoll;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(int keepAlive) {
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

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }

    public int getPayloadLength() {
        return payloadLength;
    }

    public void setPayloadLength(int payloadLength) {
        this.payloadLength = payloadLength;
    }

    public boolean isEnableMulticastGroup() {
        return enableMulticastGroup;
    }

    public void setEnableMulticastGroup(boolean enableMulticastGroup) {
        this.enableMulticastGroup = enableMulticastGroup;
    }

    public String getClusterKey() {
        return clusterKey;
    }

    public void setClusterKey(String clusterKey) {
        this.clusterKey = clusterKey;
    }

    public String getClusterMulticastGroupIp() {
        return clusterMulticastGroupIp;
    }

    public void setClusterMulticastGroupIp(String clusterMulticastGroupIp) {
        this.clusterMulticastGroupIp = clusterMulticastGroupIp;
    }

    public String[] getClusterStaticIps() {
        return clusterStaticIps;
    }

    public void setClusterStaticIps(String[] clusterStaticIps) {
        this.clusterStaticIps = clusterStaticIps;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        BrokerProperties.applicationContext = applicationContext;
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
