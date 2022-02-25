package github.microgalaxy.mqtt.broker.data;

import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.data.model.*;
import github.microgalaxy.mqtt.broker.event.model.TopicSubModel;
import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class MonitorDataManager {
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private HttpServerProperties httpServerProperties;

    /**
     * node基本信息
     **/
    @Resource
    private IgniteCache<String, NodeMetaModel> nodeMetaCache;

    /**
     * node集群状态信息
     **/
    @Resource
    private IgniteCache<String, NodeClusterModel> nodeClusterCache;

    /**
     * node性能信息（ cpu  memory  onlineStatus  运行时间  系统时间）
     **/
    @Resource
    private IgniteCache<String, NodePerformanceModel> nodePerformanceCache;

    /**
     * node运行管理（连接数  主题数  保留消息数  订阅数 共享订阅数 流入、流出消息数量 收、发流量）
     **/
    @Resource
    private IgniteCache<String, NodeMqttPerformanceModel> nodeMqttPerformanceCache;

    /**
     * 客户端管理（节点客户端信息、踢除、清除会话）
     **/
    @Resource
    private IgniteCache<String, NodeClientModel> nodeClientCache;

    /**
     * 节点客户端订阅（客户端、普通订阅和共享订阅、服务质量）
     **/
    @Resource
    private IgniteCache<String, TopicSubscribeModel> topicSubscribeCache;

    public List<NodeMetaModel> getMetaInfos() {
        List<NodeMetaModel> nodes = new ArrayList<>();
        nodeMetaCache.forEach(en -> nodes.add(en.getValue()));
        return nodes;
    }

    public void removeNodeMeta(String brokerId) {
        nodeMetaCache.remove(brokerId);
    }

    public List<NodeClusterModel> getClusterInfo() {
        List<NodeClusterModel> clusters = new ArrayList<>();
        nodeClusterCache.forEach(en -> clusters.add(en.getValue()));
        return clusters;
    }

    /**
     * 集群节点据合信息（连接数  主题数  保留消息数  订阅数 共享订阅数 流入、流出消息数量）
     *
     * @return
     */
    public NodeMqttPerformanceModel getAllMqttPerformanceInfo() {
        NodeMqttPerformanceModel model = new NodeMqttPerformanceModel();
        model.setClientNumber(0);
        model.setSubscribeTopicNumber(0);
        model.setRetainMessageNumber(0);
        model.setShareSubscribeTopicNumber(0);
        model.setMsgInNum(0L);
        model.setMsgOutNum(0L);
        model.setMsgDiscardNum(0L);
        model.setReceiveFlowTotal(0L);
        model.setSendFlowTotal(0L);
        nodeMqttPerformanceCache.forEach(en -> {
            int subscribeTopicNum = en.getValue().getSubscribeTopicMap().values()
                    .stream().mapToInt(t -> CollectionUtils.isEmpty(t) ? 0 : t.size()).sum();

            int shareSubscribeTopicNum = en.getValue().getShareSubscribeTopicMap().values()
                    .stream().mapToInt(t -> CollectionUtils.isEmpty(t) ? 0 : t.size()).sum();
            model.setClientNumber(model.getClientNumber() + en.getValue().getClientNumber());
            model.setSubscribeTopicNumber(model.getSubscribeTopicNumber() + subscribeTopicNum);
            model.setRetainMessageNumber(model.getRetainMessageNumber() + en.getValue().getRetainMessageNumber());
            model.setShareSubscribeTopicNumber(model.getShareSubscribeTopicNumber() + shareSubscribeTopicNum);
            model.setMsgInNum(model.getMsgInNum() + en.getValue().getMsgInNum());
            model.setMsgOutNum(model.getMsgOutNum() + en.getValue().getMsgOutNum());
            model.setMsgDiscardNum(model.getMsgDiscardNum() + en.getValue().getMsgDiscardNum());
            model.setReceiveFlowTotal(model.getReceiveFlowTotal() + en.getValue().getReceiveFlowTotal());
            model.setSendFlowTotal(model.getSendFlowTotal() + en.getValue().getSendFlowTotal());
        });
        return model;
    }

    public Map<String, List<TopicSubModel>> getClientSubscribeInfos(List<String> clients) {
        HashMap<String, List<TopicSubModel>> clientSubMap = new HashMap<>(clients.size());
        nodeMqttPerformanceCache.forEach(en -> {
            NodeMqttPerformanceModel nodeModel = en.getValue();
            clients.forEach(v -> {
                List<TopicSubModel> subs = Optional.ofNullable(nodeModel.getSubscribeTopicMap().get(v))
                        .orElse(new ArrayList<>(0));
                List<TopicSubModel> topicShareSubs = nodeModel.getShareSubscribeTopicMap().get(v);
                if(!CollectionUtils.isEmpty(topicShareSubs)) subs.addAll(topicShareSubs);
                clientSubMap.put(v, subs);
            });
        });
        return clientSubMap;
    }


    //持久化消息（主题）

    //主题监控

    //系统警告（集群离开、【高内存、高cpu 、高并发数、高连接数（持续时间）】）

    //用户管理、用户app管理、app鉴权管理（鉴权缓存时间）

    //监听器列表（协议+port+current连接数）
}
