package github.microgalaxy.mqtt.broker.util;

import github.microgalaxy.mqtt.broker.client.ISessionStore;
import github.microgalaxy.mqtt.broker.client.Session;
import github.microgalaxy.mqtt.broker.client.Subscribe;
import github.microgalaxy.mqtt.broker.event.IBrokerEvent;
import github.microgalaxy.mqtt.broker.event.model.*;
import github.microgalaxy.mqtt.broker.server.MqttServer;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import io.netty.util.AttributeKey;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public abstract class MqttEventUtils {
    public static void onClientConnectedEvent(String clientId, IBrokerEvent brokerEventAdapter, ISessionStore sessionStoreServer) {
        Session session = sessionStoreServer.get(clientId);
        Date now = new Date();
        MqttServer.submitTask(() -> {
            ClientConnectedModel model = new ClientConnectedModel();
            model.setClientId(session.getClientId());
            model.setUsername(session.getUsername());
            model.setKeepAlive(session.getKeepAlive());
            model.setIp(session.getIp());
            model.setProtocolVersion(session.getMqttProtocolVersion());
            model.setOccurTime(now);
            MqttEvent<MqttEventType> mqttEvent = new MqttEvent<>(MqttEventType.CLIENT_CONNECT, model);
            brokerEventAdapter.onEvent(mqttEvent);
        });
    }

    public static void onClientDisconnectedEvent(Session session, IBrokerEvent brokerEventAdapter) {
        Date now = new Date();
        MqttServer.submitTask(() -> {
            ClientDisConnectedModel model = new ClientDisConnectedModel();
            model.setClientId(session.getClientId());
            model.setUsername(session.getUsername());
            model.setIp(session.getIp());
            model.setOccurTime(now);
            MqttEvent<MqttEventType> mqttEvent = new MqttEvent<>(MqttEventType.CLIENT_DISCONNECT, model);
            brokerEventAdapter.onEvent(mqttEvent);
        });
    }

    public static void onMsgArriveEvent(Channel channel, IBrokerEvent brokerEventAdapter, ISessionStore sessionStoreServer, MqttPublishMessage msg, byte[] messageBytes) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        Session session = sessionStoreServer.get(clientId);
        Date now = new Date();
        MqttServer.submitTask(() -> {
            MsgArriveModel model = new MsgArriveModel();
            model.setClientId(session.getClientId());
            model.setUsername(session.getUsername());
            model.setTopic(msg.variableHeader().topicName());
            model.setQos(msg.fixedHeader().qosLevel().value());
            model.setRetain(msg.fixedHeader().isRetain());
            model.setPayload(messageBytes);
            model.setOccurTime(now);
            MqttEvent<MqttEventType> mqttEvent = new MqttEvent<>(MqttEventType.MSG_ARRIVE, model);
            brokerEventAdapter.onEvent(mqttEvent);
        });
    }

    public static void onMsgPushEvent(Channel channel, IBrokerEvent brokerEventAdapter, ISessionStore sessionStoreServer, MqttPublishMessage msg, byte[] messageBytes) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        Session session = sessionStoreServer.get(clientId);
        Date now = new Date();
        MqttServer.submitTask(() -> {
            MsgPushModel model = new MsgPushModel();
            model.setClientId(session.getClientId());
            model.setUsername(session.getUsername());
            model.setTopic(msg.variableHeader().topicName());
            model.setQos(msg.fixedHeader().qosLevel().value());
            model.setRetain(msg.fixedHeader().isRetain());
            model.setPayload(messageBytes);
            model.setOccurTime(now);
            MqttEvent<MqttEventType> mqttEvent = new MqttEvent<>(MqttEventType.MSG_PUSH, model);
            brokerEventAdapter.onEvent(mqttEvent);
        });
    }

    public static void onTopicSubEvent(String clientId, Collection<Subscribe> subscribes, IBrokerEvent brokerEventAdapter, ISessionStore sessionStoreServer) {
        Session session = sessionStoreServer.get(clientId);
        Date now = new Date();
        MqttServer.submitTask(() -> {
            List<TopicSubModel> models = subscribes.stream().map(t -> {
                TopicSubModel model = new TopicSubModel();
                model.setClientId(session.getClientId());
                model.setUsername(session.getUsername());
                model.setTopic(t.getTopic());
                model.setQos(t.getQos().value());
                model.setOccurTime(now);
                return model;
            }).collect(Collectors.toList());
            MqttEvent<MqttEventType> mqttEvent = new MqttEvent<>(MqttEventType.SUB_TOPIC, models);
            brokerEventAdapter.onEvent(mqttEvent);
        });
    }

    public static void onTopicUnSubEvent(String clientId, Collection<String> topics, IBrokerEvent brokerEventAdapter, ISessionStore sessionStoreServer) {
        Session session = sessionStoreServer.get(clientId);
        Date now = new Date();
        MqttServer.submitTask(() -> {
            List<TopicUnSubModel> models = topics.stream().map(t -> {
                TopicUnSubModel model = new TopicUnSubModel();
                model.setClientId(session.getClientId());
                model.setUsername(session.getUsername());
                model.setTopic(t);
                model.setOccurTime(now);
                return model;
            }).collect(Collectors.toList());
            MqttEvent<MqttEventType> mqttEvent = new MqttEvent<>(MqttEventType.UNSUB_TOPIC, models);
            brokerEventAdapter.onEvent(mqttEvent);
        });
    }
}
