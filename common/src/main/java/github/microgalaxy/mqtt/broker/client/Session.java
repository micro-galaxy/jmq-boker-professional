package github.microgalaxy.mqtt.broker.client;

import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.io.Serializable;

/**
 * mqtt客户端Session
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public final class Session implements Serializable {
    private static final long serialVersionUID = 1L;
    @QuerySqlField
    private final String jmqId;
    @QuerySqlField(index = true)
    private final String clientId;
    @QuerySqlField
    private final String username;
    private final String ip;
    private final transient Channel channel;
    private final Integer keepAlive;
    private final boolean cleanSession;
    private final MqttPublishMessage willMessage;
    private final MqttVersion mqttProtocolVersion;
    private final Long createTimestamp;

    public Session(String jmqId, String clientId, String username, String ip, Channel channel, Integer keepAlive,
                   boolean cleanSession, MqttPublishMessage willMessage, MqttVersion mqttProtocolVersion, Long createTimestamp) {
        this.jmqId = jmqId;
        this.clientId = clientId;
        this.username = username;
        this.ip = ip;
        this.channel = channel;
        this.keepAlive = keepAlive;
        this.cleanSession = cleanSession;
        this.willMessage = willMessage;
        this.mqttProtocolVersion = mqttProtocolVersion;
        this.createTimestamp = createTimestamp;
    }

    public String getJmqId() {
        return jmqId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getUsername() {
        return username;
    }

    public String getIp() {
        return ip;
    }

    public Channel getChannel() {
        return channel;
    }

    public Integer getKeepAlive() {
        return keepAlive;
    }

    public boolean isCleanSession() {
        return cleanSession;
    }

    public MqttPublishMessage getWillMessage() {
        return willMessage;
    }

    public MqttVersion getMqttProtocolVersion() {
        return mqttProtocolVersion;
    }

    public Long getCreateTimestamp() {
        return createTimestamp;
    }
}
