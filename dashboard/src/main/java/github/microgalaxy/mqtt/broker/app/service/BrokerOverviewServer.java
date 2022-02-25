package github.microgalaxy.mqtt.broker.app.service;

import github.microgalaxy.mqtt.broker.app.model.RealTimeOverviewModel;
import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.data.MonitorDataManager;
import github.microgalaxy.mqtt.broker.data.model.NodeClusterModel;
import github.microgalaxy.mqtt.broker.data.model.NodeMetaModel;
import github.microgalaxy.mqtt.broker.data.model.NodeMqttPerformanceModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Service
public class BrokerOverviewServer {
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private MonitorDataManager monitorDataManager;


    public RealTimeOverviewModel realTimeOverview() {
        List<NodeMetaModel> metaInfos = monitorDataManager.getMetaInfos();
        List<NodeClusterModel> clusterInfos = monitorDataManager.getClusterInfo().stream()
                .filter(NodeClusterModel::isActive)
                .sorted((a, b) -> (int) (a.getOccurTime().getTime() - b.getOccurTime().getTime())).collect(Collectors.toList());

        RealTimeOverviewModel model = new RealTimeOverviewModel();
        model.setBrokerMetaNum(metaInfos.size());
        model.setOnlineBrokerNum(clusterInfos.size());
        model.setEarliestClusterTime(clusterInfos.get(0).getOccurTime());
        model.setBrokerVersion(brokerProperties.getVersion());

        NodeMqttPerformanceModel allPerformanceInfo = monitorDataManager.getAllMqttPerformanceInfo();
        model.setInMsgNum(allPerformanceInfo.getMsgInNum());
        model.setOutMsgNum(allPerformanceInfo.getMsgOutNum());
        model.setClientNum(allPerformanceInfo.getClientNumber());
        model.setSubscribeNum(allPerformanceInfo.getSubscribeTopicNumber() + allPerformanceInfo.getShareSubscribeTopicNumber());
        return model;
    }
}
