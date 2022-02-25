package github.microgalaxy.mqtt.broker.event;

import github.microgalaxy.mqtt.broker.event.model.MqttEvent;
import github.microgalaxy.mqtt.broker.event.model.MqttEventType;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Mqtt 消息事件
 * <p>
 * 客户端连接成功、客户端断开连接、消息流入、消息流出
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public abstract class AbstractMqttEvent<T extends MqttEvent<MqttEventType>> implements IBrokerEvent<T> {
    private final Map<MqttEventType, Consumer<T>> evtMap = new HashMap<>();

    protected AbstractMqttEvent() {
        evtMap.put(MqttEventType.CLIENT_CONNECT, this::onConnected);
        evtMap.put(MqttEventType.CLIENT_DISCONNECT, this::onDisConnected);
        evtMap.put(MqttEventType.MSG_ARRIVE, this::onMessageArrived);
        evtMap.put(MqttEventType.MSG_PUSH, this::onMessageDelivered);
        evtMap.put(MqttEventType.MSG_DISCARD, this::onMessageDiscard);
        evtMap.put(MqttEventType.SUB_TOPIC, this::onSubTopic);
        evtMap.put(MqttEventType.UNSUB_TOPIC, this::onUnSubTopic);
    }

    @Override
    public void onEvent(T event) {
        evtMap.get(event.getType()).accept(event);
    }

    /**
     * 客户端连接成功事件
     *
     * @param clientId
     * @param username
     * @param keepAlive
     * @param ip
     * @param protocolVersion
     * @param connectedTime
     */
    protected abstract void onConnected(T event /* String clientId, String username, int keepAlive,
                     String ip, MqttVersion protocolVersion, Date connectedTime */);

    /**
     * 客户端断开连接事件
     *
     * @param clientId
     * @param username
     * @param disConnectedTime
     */
    protected abstract void onDisConnected(T event /* String clientId, String username, Date disConnectedTime */);

    /**
     * 客户端消息抵达事件
     *
     * @param clientId
     * @param username
     * @param topic
     * @param qos
     * @param retain
     * @param payload
     * @param arriveTime
     */
    protected abstract void onMessageArrived(T event /* String clientId, String username, String topic,
                          int qos, boolean retain, byte[] payload, Date arriveTime */);

    /**
     * 消息发送事件
     *
     * @param clientId
     * @param username
     * @param targetClientId
     * @param targetClientName
     * @param topic
     * @param qos
     * @param retain
     * @param payload
     * @param arriveTime
     */
    protected abstract void onMessageDelivered(T event /* String clientId, String username, String topic, String targetClientId,
                            String targetClientName, int qos, boolean retain, byte[] payload, Date pushTime */);

    /**
     * 消息丢弃事件
     *
     * @param clientId
     * @param username
     * @param targetClientId
     * @param targetClientName
     * @param topic
     * @param qos
     * @param retain
     * @param payload
     * @param arriveTime
     */
    protected abstract void onMessageDiscard(T event /* String clientId, String username, String topic,
                          int qos, boolean retain, byte[] payload, Date arriveTime */);

    /**
     * @param clientId
     * @param username
     * @param topic
     * @param qos
     * @param occurTime
     */
    protected abstract void onSubTopic(T event);

    /**
     * @param clientId
     * @param username
     * @param topic
     * @param qos
     * @param occurTime
     */
    protected abstract void onUnSubTopic(T event);
}
