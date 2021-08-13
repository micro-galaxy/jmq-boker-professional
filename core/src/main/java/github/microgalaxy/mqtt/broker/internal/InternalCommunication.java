package github.microgalaxy.mqtt.broker.internal;

import github.microgalaxy.mqtt.broker.protocol.MqttPublish;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.mqtt.MqttMessageBuilders;
import io.netty.handler.codec.mqtt.MqttPublishMessage;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.cluster.ClusterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;

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
        String igniteInstanceName = igniteMessaging.clusterGroup().ignite().name();
        //TODO 需要修复消息丢失问题
        System.out.println("=======================================================分布式消息" + message.toString());
        if (!message.getJmqIds().contains(igniteInstanceName)) return;
        MqttPublishMessage publishMessage = MqttMessageBuilders.publish()
                .topicName(message.getTopic())
                .qos(message.getQos())
                .payload(Unpooled.copiedBuffer(message.getPayload()))
                .retained(message.isRetain())
                .build();
        mqttPublish.sendPublishMessage(publishMessage, mqttPublish.getTargetSubscribe(message.getTopic()));
    }

    @Override
    public void sendInternalMessage(InternalMessage message) {
        if (CollectionUtils.isEmpty(igniteMessaging.clusterGroup().nodes())) return;
        igniteMessaging.send(INTERNAL_TOPIC, message);
    }
}
