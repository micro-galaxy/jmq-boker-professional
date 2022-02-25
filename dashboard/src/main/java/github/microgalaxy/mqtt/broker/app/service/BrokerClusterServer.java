package github.microgalaxy.mqtt.broker.app.service;

import github.microgalaxy.mqtt.broker.app.model.ClusterDetailModel;
import github.microgalaxy.mqtt.broker.app.model.NodeForm;
import github.microgalaxy.mqtt.broker.client.BrokerClusterManager;
import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.data.MonitorDataManager;
import github.microgalaxy.mqtt.broker.data.model.NodeClusterModel;
import github.microgalaxy.mqtt.broker.data.model.NodeMetaModel;
import github.microgalaxy.mqtt.broker.data.model.NodeMqttPerformanceModel;
import github.microgalaxy.mqtt.broker.internal.model.CtrlType;
import github.microgalaxy.mqtt.broker.internal.InternalCommunicationAdapter;
import github.microgalaxy.mqtt.broker.internal.model.InternalIgniteCtrlMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Service
public class BrokerClusterServer {
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private InternalCommunicationAdapter<Object> internalCommunicationAdapter;
    @Autowired
    private BrokerClusterManager brokerClusterManager;
    @Autowired
    private MonitorDataManager monitorDataManager;

    public void joinClusterIp(NodeForm model) {
        if (isCurrentNode(brokerProperties, model)) {
            brokerClusterManager.joinClusterIp(model.getIp());
            return;
        }
        InternalIgniteCtrlMessage<CtrlType, NodeForm> ctrlMessage = new InternalIgniteCtrlMessage<>(CtrlType.JOIN_CLUSTER_IP, model);
        internalCommunicationAdapter.sendInternalMessage(ctrlMessage);
    }

    public void joinClusterMulticastIp(NodeForm model) {
        if (isCurrentNode(brokerProperties, model)) {
            brokerClusterManager.joinClusterMulticastIp(model.getIp());
            return;
        }
        InternalIgniteCtrlMessage<CtrlType, NodeForm> ctrlMessage = new InternalIgniteCtrlMessage<>(CtrlType.JOIN_CLUSTER_MULTICAST_IP, model);
        internalCommunicationAdapter.sendInternalMessage(ctrlMessage);
    }

    public void leftCluster(NodeForm model) {
        if (isCurrentNode(brokerProperties, model)) {
            brokerClusterManager.leftCluster();
            return;
        }
        InternalIgniteCtrlMessage<CtrlType, NodeForm> ctrlMessage = new InternalIgniteCtrlMessage<>(CtrlType.LEFT_CLUSTER, model);
        internalCommunicationAdapter.sendInternalMessage(ctrlMessage);
    }

    public void reconnectCluster(NodeForm model) {
        if (isCurrentNode(brokerProperties, model)) {
            brokerClusterManager.reconnectCluster();
            return;
        }
        InternalIgniteCtrlMessage<CtrlType, NodeForm> ctrlMessage = new InternalIgniteCtrlMessage<>(CtrlType.RECONNECT_CLUSTER, model);
        internalCommunicationAdapter.sendInternalMessage(ctrlMessage);
    }

    private boolean isCurrentNode(BrokerProperties brokerProperties, NodeForm model) {
        return Objects.equals(brokerProperties.getBrokerId(), model.getBrokerId());
    }

    public List<NodeClusterModel> getClusterInfo() {
       return monitorDataManager.getClusterInfo();
    }

    public ClusterDetailModel getClusterDetail() {
        List<NodeMetaModel> metaInfos = monitorDataManager.getMetaInfos();
        List<NodeClusterModel> clusterInfos = monitorDataManager.getClusterInfo();
        NodeMqttPerformanceModel allPerformanceInfo = monitorDataManager.getAllMqttPerformanceInfo();

        ClusterDetailModel model = new ClusterDetailModel();
        model.setBrokerMetaNum(metaInfos.size());
        model.setBrokerVersion(brokerProperties.getVersion());
        model.setClientNum(allPerformanceInfo.getClientNumber());
        model.setSubscribeNum(allPerformanceInfo.getSubscribeTopicNumber() + allPerformanceInfo.getShareSubscribeTopicNumber());
        model.setNodes(clusterInfos.stream().map(v ->{
            ClusterDetailModel.CusterNode custerNode = new ClusterDetailModel.CusterNode();
            custerNode.setBrokerId(v.getBrokerId());
            custerNode.setIp(v.getIp());
            custerNode.setActive(v.isActive());
            custerNode.setOccurTime(v.getOccurTime());
            return custerNode;
        }).collect(Collectors.toList()));
        return model;
    }
}
