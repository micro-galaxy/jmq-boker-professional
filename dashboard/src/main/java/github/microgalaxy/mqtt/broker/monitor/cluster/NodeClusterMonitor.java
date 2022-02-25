package github.microgalaxy.mqtt.broker.monitor.cluster;

import github.microgalaxy.mqtt.broker.config.BrokerConstant;
import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.data.model.NodeClusterModel;
import github.microgalaxy.mqtt.broker.event.AbstractClusterEvent;
import github.microgalaxy.mqtt.broker.util.TopicUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cluster.ClusterNode;
import org.apache.ignite.events.DiscoveryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class NodeClusterMonitor<T extends DiscoveryEvent> extends AbstractClusterEvent<T> {
    @Autowired
    private BrokerProperties brokerProperties;
    @Resource
    private Ignite ignite;
    @Resource
    private IgniteCache<String, NodeClusterModel> nodeClusterCache;

    @PostConstruct
    private void initClusterInfo() {
        Collection<ClusterNode> rmtNodes = ignite.cluster().forRemotes().nodes();
        if (CollectionUtils.isEmpty(rmtNodes)) {
            NodeClusterModel model = new NodeClusterModel();
            model.setBrokerId(brokerProperties.getBrokerId());
            model.setIp(TopicUtils.getMacIp());
            model.setPort(ignite.cluster().localNode().attribute(BrokerConstant.BROKER_MQTT_PORT_KEY));
            model.setActive(true);
            model.setOccurTime(new Date());
            nodeClusterCache.put(brokerProperties.getBrokerId(), model);
        }
    }

    @Override
    protected void onNodeJoined(T event) {
        String rmtBrokerId = event.eventNode().attribute(BrokerConstant.BROKER_KEY);
        if (Objects.equals(rmtBrokerId, brokerProperties.getBrokerId())) {
            logger.error("=============================================================================\r\n");
            logger.error("Broker id already exists, brokerId: {}. The remote node will stop.", rmtBrokerId);
            ignite.cluster().stopNodes(Collections.singletonList(event.eventNode().id()));
            return;
        }
        NodeClusterModel model = new NodeClusterModel();
        model.setBrokerId(rmtBrokerId);
        model.setIp(event.eventNode().attribute(BrokerConstant.BROKER_NODE_IP_KEY));
        model.setPort(event.eventNode().attribute(BrokerConstant.BROKER_MQTT_PORT_KEY));
        model.setActive(true);
        model.setOccurTime(new Date());
        nodeClusterCache.put(rmtBrokerId, model);
    }

    @Override
    protected void onNodeLeft(T event) {
        clusterDisconnect(event);
    }

    @Override
    protected void onNodeFailed(T event) {
        clusterDisconnect(event);
    }

    @Override
    protected void onClientNodeDisconnected(T event) {
        clusterDisconnect(event);
    }

    @Override
    protected void onClientNodeReconnected(T event) {
        String rmtBrokerId = event.eventNode().attribute(BrokerConstant.BROKER_KEY);
        NodeClusterModel model = nodeClusterCache.get(rmtBrokerId);
        model.setActive(true);
        model.setOccurTime(new Date());
        nodeClusterCache.put(rmtBrokerId, model);
    }

    private void clusterDisconnect(T event) {
        String rmtBrokerId = event.eventNode().attribute(BrokerConstant.BROKER_KEY);
        NodeClusterModel model = nodeClusterCache.get(rmtBrokerId);
        if (ObjectUtils.isEmpty(model)) return;
        model.setActive(false);
        model.setOccurTime(new Date());
        model.setReason(event.message());
        nodeClusterCache.put(rmtBrokerId, model);
        //TODO clear client subTopic

    }
}
