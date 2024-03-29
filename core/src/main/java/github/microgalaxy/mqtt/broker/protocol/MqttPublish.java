package github.microgalaxy.mqtt.broker.protocol;

import github.microgalaxy.mqtt.broker.client.ISessionStore;
import github.microgalaxy.mqtt.broker.client.ISubscribeStore;
import github.microgalaxy.mqtt.broker.client.Session;
import github.microgalaxy.mqtt.broker.client.Subscribe;
import github.microgalaxy.mqtt.broker.event.IBrokerEvent;
import github.microgalaxy.mqtt.broker.handler.MqttException;
import github.microgalaxy.mqtt.broker.internal.InternalCommunicationAdapter;
import github.microgalaxy.mqtt.broker.internal.model.InternalMqttMessage;
import github.microgalaxy.mqtt.broker.message.*;
import github.microgalaxy.mqtt.broker.util.MqttEventUtils;
import github.microgalaxy.mqtt.broker.util.TopicUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.*;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.Collection;

/**
 * 发布消息
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class MqttPublish<T extends MqttMessageType, M extends MqttPublishMessage> extends AbstractMqttMsgProtocol<T, M> {
    @Autowired
    @Lazy
    private IBrokerEvent brokerEventAdapter;
    @Autowired
    private ISessionStore sessionStoreServer;
    @Autowired
    private IMessagePacketId messagePacketIdServer;
    @Autowired
    private ISubscribeStore subscribeStoreServer;
    @Autowired
    private InternalCommunicationAdapter<Object> internalCommunicationServer;
    @Autowired
    private IDupPublishMessage dupPublishMessageServer;
    @Autowired
    private IDupRetainMessage dupRetainMessageServer;

    /**
     * 发布消息
     *
     * @param channel
     * @param msg
     */
    @Override
    public void onMqttMsg(Channel channel, M msg) {
        String topic = msg.variableHeader().topicName();
        if (!TopicUtils.validTopic(topic)) throw new MqttException(
                (int) MqttConnectReturnCode.CONNECTION_REFUSED_MALFORMED_PACKET.byteValue(),
                false, "<== PUBLISH - Topic is not valid");

        MqttQoS mqttQoS = msg.fixedHeader().qosLevel();
        ByteBuf payload = msg.payload();
        byte[] messageBytes = new byte[payload.readableBytes()];
        payload.getBytes(payload.readerIndex(), messageBytes);
        MqttPublishMessage publishMessage = MqttMessageBuilders.publish()
                .topicName(topic)
                .messageId(msg.variableHeader().packetId())
                .qos(mqttQoS)
                .payload(payload)
                .retained(false)
                .build();
        if (MqttQoS.AT_LEAST_ONCE == mqttQoS) {
            sendPubAckMessage(channel, msg.variableHeader().packetId());
        }
        if (MqttQoS.EXACTLY_ONCE == mqttQoS) {
            sendPubRecMessage(channel, msg.variableHeader().packetId());
        }
        MqttEventUtils.onMsgArriveEvent(channel, brokerEventAdapter, sessionStoreServer, msg,messageBytes);

        Collection<Subscribe> subscribes = getTargetSubscribe(publishMessage.variableHeader().topicName());
        if (!CollectionUtils.isEmpty(subscribes)) {
            InternalMqttMessage internalMqttMessage = new InternalMqttMessage();
            internalMqttMessage.setSubscribeList(subscribes);
            internalMqttMessage.setTopic(publishMessage.variableHeader().topicName());
            internalMqttMessage.setQos(publishMessage.fixedHeader().qosLevel());
            internalMqttMessage.setPayload(messageBytes);
            internalMqttMessage.setRetain(false);
            internalMqttMessage.setDup(false);
            internalCommunicationServer.sendInternalMessage(internalMqttMessage);
            sendPublishMessage(publishMessage, subscribes);
        }
        if (msg.fixedHeader().isRetain()) {
            if (messageBytes.length == 0) {
                dupRetainMessageServer.remove(topic);
            } else {
                RetainMessage retainMessage = new RetainMessage(topic, mqttQoS, messageBytes);
                dupRetainMessageServer.put(topic, retainMessage);
            }
        }
    }

    public Collection<Subscribe> getTargetSubscribe(String topicName) {
        Collection<Subscribe> shareSubscribes = subscribeStoreServer.matchShareTopic(topicName);
        Collection<Subscribe> subscribes = subscribeStoreServer.matchTopic(topicName);
        return TopicUtils.filterTopic(shareSubscribes, subscribes);
    }

    @Override
    public MqttMessageType getHandleType() {
        return T.PUBLISH;
    }

    @Override
    public void onHandlerError(Channel channel, M msg, MqttException ex) {
        MqttQoS mqttQoS = msg.fixedHeader().qosLevel();
        MqttMessage mqttErrorMessage = null;
        if (MqttQoS.AT_LEAST_ONCE == mqttQoS) {
            mqttErrorMessage = MqttMessageBuilders.pubAck()
                    .packetId(msg.variableHeader().packetId())
                    .reasonCode((byte) ex.getReasonCode())
                    .build();
        }
        if (MqttQoS.EXACTLY_ONCE == mqttQoS) {
            mqttErrorMessage = MqttMessageFactory.newMessage(
                    new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                    new MqttPubReplyMessageVariableHeader(msg.variableHeader().packetId(), (byte) ex.getReasonCode(), MqttProperties.NO_PROPERTIES),
                    null);
        }
        channel.writeAndFlush(mqttErrorMessage);
        if (ex.isDisConnect()) {
            channel.close();
            log.info(ex.getMessage());
        }
    }

    private void sendPubAckMessage(Channel channel, int packetId) {
        MqttMessage pubAckMessage = MqttMessageBuilders.pubAck()
                .packetId(packetId)
                .build();
        channel.writeAndFlush(pubAckMessage);
        if (log.isDebugEnabled())
            log.debug("==> SEND - Send pubAck packet: clientId:{}, messageId:{}",
                    channel.attr(AttributeKey.valueOf("clientId")).get(), packetId);
    }

    private void sendPubRecMessage(Channel channel, int packetId) {
        MqttMessage pubRecMessage = MqttMessageFactory.newMessage(
                new MqttFixedHeader(MqttMessageType.PUBREC, false, MqttQoS.AT_MOST_ONCE, false, 0),
                MqttMessageIdVariableHeader.from(packetId), null);
        channel.writeAndFlush(pubRecMessage);
        if (log.isDebugEnabled())
            log.debug("==> SEND - Send pubRec packet: clientId:{}, messageId:{}",
                    channel.attr(AttributeKey.valueOf("clientId")).get(), packetId);
    }

    public void sendPublishMessage(MqttPublishMessage publishMessage, Collection<Subscribe> sendSubscribes) {
        ByteBuf payload = publishMessage.payload();
        byte[] messageBytes = new byte[payload.readableBytes()];
        payload.getBytes(payload.readableBytes(), messageBytes);
        sendSubscribes.forEach(s -> {
            Session session = sessionStoreServer.get(s.getClientId());
            if (ObjectUtils.isEmpty(session)) return;
            MqttQoS targetQos = MqttQoS.valueOf(Math.min(s.getQos().value(), publishMessage.fixedHeader().qosLevel().value()));
            int messageId = MqttQoS.AT_MOST_ONCE == targetQos ? 0 :
                    messagePacketIdServer.nextMessageId((MqttVersion) session.getChannel().attr(AttributeKey.valueOf("mqttVersion")).get());
            MqttPublishMessage message = MqttMessageBuilders.publish()
                    .topicName(publishMessage.variableHeader().topicName())
                    .messageId(messageId)
                    .qos(targetQos)
                    .retained(publishMessage.fixedHeader().isRetain())
                    .payload(Unpooled.copiedBuffer(payload))
                    .build();
            if (targetQos.value() > MqttQoS.AT_MOST_ONCE.value()) {
                DupPublishMessage dupPublishMessage = new DupPublishMessage(s.getClientId(), s.getTopic(), targetQos, messageId, messageBytes);
                dupPublishMessageServer.put(s.getClientId(), dupPublishMessage);
            }
            session.getChannel().writeAndFlush(message);
            MqttEventUtils.onMsgPushEvent(session.getChannel(), brokerEventAdapter, sessionStoreServer, message,messageBytes);
            if (log.isDebugEnabled())
                log.debug("==> PUBLISH - Publish a message: clientId:{}, topic:{}, qos:{}, messageId:{}",
                        s.getClientId(), s.getTopic(), targetQos.value(), messageId);
        });
    }

    public void sendWillMessage(Channel channel) {
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        Session session = sessionStoreServer.get(clientId);
        if (ObjectUtils.isEmpty(session) || ObjectUtils.isEmpty(session.getWillMessage())) return;
        sendPublishMessage(session.getWillMessage(), getTargetSubscribe(session.getWillMessage().variableHeader().topicName()));
    }
}
