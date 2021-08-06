package github.microgalaxy.mqtt.broker.internal;

import github.microgalaxy.mqtt.broker.protocol.MqttPublish;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.apache.ignite.IgniteMessaging;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class InternalCommunication implements IInternalCommunication {
    private static final String INTERNAL_TOPIC = "ignite-internal-topic";
    @Resource
    private IgniteMessaging igniteMessaging;
    @Autowired
    private MqttPublish mqttPublish;

    @PostConstruct
    private void igniteListen() {
        igniteMessaging.localListen(INTERNAL_TOPIC, (nodeId, msg) -> {
            InternalMessage internalMessage = (InternalMessage) msg;
            onInternalMessage(internalMessage);
            return true;
        });
    }


    @Override
    public void onInternalMessage(InternalMessage message) {
        MqttPublishMessage publishMessage = MqttMessageBuilders.publish()
                .topicName(message.getTopic())
                .qos(message.getQos())
                .payload(Unpooled.copiedBuffer(message.getPayload()))
                .retained(message.isRetain())
                .build();
        mqttPublish.sendPublishMessage(publishMessage);
    }

    @Override
    public void sendInternalMessage(InternalMessage message) {
        if (CollectionUtils.isEmpty(igniteMessaging.clusterGroup().nodes())) return;
        igniteMessaging.send(INTERNAL_TOPIC, message);
    }
}
