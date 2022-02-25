package github.microgalaxy.mqtt.broker.data.model;


import github.microgalaxy.mqtt.broker.event.model.TopicSubModel;
import lombok.Data;
import org.apache.ignite.cache.query.annotations.QuerySqlField;

import java.util.List;
import java.util.Map;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 * <p>
 * node运行管理（连接数  主题数  保留消息数  订阅数 共享订阅数 流入、流出消息数量）
 */
@Data
public class NodeMqttPerformanceModel {
    @QuerySqlField
    private String brokerId;

    private int clientNumber;

    private Map<String, List<TopicSubModel>> subscribeTopicMap;

    private int retainMessageNumber;

    private Map<String, List<TopicSubModel>> shareSubscribeTopicMap;

    private Long msgInNum;

    private Long msgOutNum;

    private Long msgDiscardNum;

    private Long receiveFlowTotal;
//    /**
//     * timeStamp : rate
//     */
//    private LRU<Long, Double> receiveFlows = new LRU<>(60);

    private Long sendFlowTotal;
//    /**
//     * timeStamp : rate
//     */
//    private LRU<Long, Double> sendFlows = new LRU<>(60);

    private transient int subscribeTopicNumber;

    private transient int shareSubscribeTopicNumber;
}
