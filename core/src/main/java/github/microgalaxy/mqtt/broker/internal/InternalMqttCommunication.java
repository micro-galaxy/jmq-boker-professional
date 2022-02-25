package github.microgalaxy.mqtt.broker.internal;

import github.microgalaxy.mqtt.broker.internal.model.InternalMqttMessage;
import github.microgalaxy.mqtt.broker.protocol.MqttPublish;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttMessageType;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.apache.ignite.IgniteMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class InternalMqttCommunication<T extends InternalMqttMessage> implements IInternalCommunication<T> {
    @Resource
    private IgniteMessaging igniteMessaging;
    @Autowired
    private MqttPublish<MqttMessageType, MqttPublishMessage> mqttPublish;

    @Override
    public void onInternalMessage(T message) {
        String igniteInstanceName = igniteMessaging.clusterGroup().ignite().name();
        boolean currentNode = message.getSubscribeList().stream().anyMatch(m -> Objects.equals(m.getJmqId(), igniteInstanceName));
        if (!currentNode) return;
        MqttPublishMessage publishMessage = MqttMessageBuilders.publish()
                .topicName(message.getTopic())
                .qos(message.getQos())
                .payload(Unpooled.copiedBuffer(message.getPayload()))
                .retained(message.isRetain())
                .build();
        mqttPublish.sendPublishMessage(publishMessage, message.getSubscribeList());
    }
}
