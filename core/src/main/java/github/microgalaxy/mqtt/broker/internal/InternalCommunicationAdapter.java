package github.microgalaxy.mqtt.broker.internal;

import github.microgalaxy.mqtt.broker.internal.model.CtrlType;
import github.microgalaxy.mqtt.broker.internal.model.InternalIgniteCtrlMessage;
import github.microgalaxy.mqtt.broker.internal.model.InternalMqttMessage;
import github.microgalaxy.mqtt.broker.protocol.MqttConnect;
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
public class InternalCommunicationAdapter<T> implements IInternalCommunication<T> {
    public static final String INTERNAL_TOPIC = "ignite-internal-topic";
    @Resource
    private IgniteMessaging igniteMessaging;
    @Autowired
    private IInternalCommunication<InternalMqttMessage> internalMqttCommunication;
    @Autowired
    private IInternalCommunication<Object> internalCtrlCommunication;
    @Autowired
    private MqttConnect mqttConnect;


    @PostConstruct
    private void igniteListen() {
        igniteMessaging.localListen(INTERNAL_TOPIC, (nodeId, msg) -> {
            onInternalMessage((T) msg);
            return true;
        });
    }


    @Override
    public void onInternalMessage(T message) {
        if (message instanceof InternalMqttMessage)
            internalMqttCommunication.onInternalMessage((InternalMqttMessage) message);
        if (message instanceof InternalIgniteCtrlMessage) {
            if (CtrlType.REMOVE_SESSION.name().equals(((InternalIgniteCtrlMessage<CtrlType, Object>) message).getType().name())) {
                mqttConnect.removeGlobalSession(((InternalIgniteCtrlMessage<CtrlType, String>) message).getParams());
            }else {
                internalCtrlCommunication.onInternalMessage(message);
            }
        }
    }

    public void sendInternalMessage(T message) {
        if (CollectionUtils.isEmpty(igniteMessaging.clusterGroup().nodes())) return;
        igniteMessaging.send(InternalCommunicationAdapter.INTERNAL_TOPIC, message);
    }
}
