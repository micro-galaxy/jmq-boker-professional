package github.microgalaxy.mqtt.broker.internal.model;


import github.microgalaxy.mqtt.broker.client.Subscribe;
import io.netty.handler.codec.mqtt.MqttQoS;

import java.io.Serializable;
import java.util.Collection;

/**
 * ignite内部消息
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public final class InternalMqttMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Collection<Subscribe> subscribeList;

    private String topic;

    private MqttQoS qos;

    private byte[] payload;

    private boolean retain;

    private boolean dup;

    public Collection<Subscribe> getSubscribeList() {
        return subscribeList;
    }

    public void setSubscribeList(Collection<Subscribe> subscribeList) {
        this.subscribeList = subscribeList;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public MqttQoS getQos() {
        return qos;
    }

    public void setQos(MqttQoS qos) {
        this.qos = qos;
    }

    public byte[] getPayload() {
        return payload;
    }

    public void setPayload(byte[] payload) {
        this.payload = payload;
    }

    public boolean isRetain() {
        return retain;
    }

    public void setRetain(boolean retain) {
        this.retain = retain;
    }

    public boolean isDup() {
        return dup;
    }

    public void setDup(boolean dup) {
        this.dup = dup;
    }
}
