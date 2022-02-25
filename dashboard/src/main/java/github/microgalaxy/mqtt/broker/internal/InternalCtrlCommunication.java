package github.microgalaxy.mqtt.broker.internal;

import github.microgalaxy.mqtt.broker.app.model.NodeForm;
import github.microgalaxy.mqtt.broker.client.IBrokerCluster;
import github.microgalaxy.mqtt.broker.internal.model.CtrlType;
import github.microgalaxy.mqtt.broker.internal.model.InternalIgniteCtrlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class InternalCtrlCommunication<T extends InternalIgniteCtrlMessage<CtrlType,Object>> implements IInternalCommunication<T> {
    private final Map<CtrlType, Consumer<NodeForm>> clusterCtrlMap = new HashMap<>();
    @Autowired
    private IBrokerCluster brokerClusterManager;

    @PostConstruct
    private void initClusterCtrlMap() {
        clusterCtrlMap.put(CtrlType.JOIN_CLUSTER_IP, (nodeForm) -> brokerClusterManager.joinClusterIp(nodeForm.getIp()));
        clusterCtrlMap.put(CtrlType.JOIN_CLUSTER_MULTICAST_IP, (nodeForm) -> brokerClusterManager.joinClusterMulticastIp(nodeForm.getIp()));
        clusterCtrlMap.put(CtrlType.LEFT_CLUSTER, (nodeForm) -> brokerClusterManager.leftCluster());
        clusterCtrlMap.put(CtrlType.RECONNECT_CLUSTER, (nodeForm) -> brokerClusterManager.reconnectCluster());
    }

    @Override
    public void onInternalMessage(T message) {
        if (message.getType() instanceof CtrlType) clusterCtrl(message);
    }

    private void clusterCtrl(T message) {
        Consumer<NodeForm> clusterCtrlConsumer = clusterCtrlMap.get(((CtrlType) message.getType()));
        if (!ObjectUtils.isEmpty(clusterCtrlConsumer)) clusterCtrlConsumer.accept((NodeForm) message.getParams());
    }

}
