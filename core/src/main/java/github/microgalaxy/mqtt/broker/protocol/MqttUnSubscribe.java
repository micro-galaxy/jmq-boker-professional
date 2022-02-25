package github.microgalaxy.mqtt.broker.protocol;

import github.microgalaxy.mqtt.broker.client.ISessionStore;
import github.microgalaxy.mqtt.broker.client.ISubscribeStore;
import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.event.IBrokerEvent;
import github.microgalaxy.mqtt.broker.util.MqttEventUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttUnsubAckMessage;
import io.netty.handler.codec.mqtt.MqttUnsubscribeMessage;
import io.netty.util.AttributeKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * unsubscribe
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class MqttUnSubscribe<T extends MqttMessageType, M extends MqttUnsubscribeMessage> extends AbstractMqttMsgProtocol<T, M> {
    @Autowired
    @Lazy
    private IBrokerEvent brokerEventAdapter;
    @Autowired
    private ISessionStore sessionServer;
    @Autowired
    private ISubscribeStore subscribeStoreServer;

    /**
     * unsubscribe message
     *
     * @param channel
     * @param msg
     */
    @Override
    public void onMqttMsg(Channel channel, M msg) {
        List<String> topics = msg.payload().topics();
        String clientId = (String) channel.attr(AttributeKey.valueOf("clientId")).get();
        Collection<String> removeList = subscribeStoreServer.remove(clientId, topics);
        if (log.isDebugEnabled())
            log.debug("UNSUBSCRIBE - Client unsubscribe message arrives: clientId:{}, topics:{}", clientId, removeList);
        MqttUnsubAckMessage unsubAckMessage = MqttMessageBuilders.unsubAck()
                .packetId(msg.variableHeader().messageId())
                .build();
        channel.writeAndFlush(unsubAckMessage);
        MqttEventUtils.onTopicUnSubEvent(clientId,removeList,brokerEventAdapter,sessionServer);
    }

    @Override
    public MqttMessageType getHandleType() {
        return T.UNSUBSCRIBE;
    }
}
