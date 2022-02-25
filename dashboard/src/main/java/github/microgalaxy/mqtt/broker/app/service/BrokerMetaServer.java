package github.microgalaxy.mqtt.broker.app.service;

import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.data.MonitorDataManager;
import github.microgalaxy.mqtt.broker.data.model.NodeMetaModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Service
public class BrokerMetaServer {
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private MonitorDataManager monitorDataManager;

    public List<NodeMetaModel> getMetaInfos() {
       return monitorDataManager.getMetaInfos();
    }

    public void remove(String brokerId) {
        monitorDataManager.removeNodeMeta(brokerId);
    }
}
