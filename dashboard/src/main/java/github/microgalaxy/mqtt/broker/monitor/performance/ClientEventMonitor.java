package github.microgalaxy.mqtt.broker.monitor.performance;

import github.microgalaxy.mqtt.broker.client.Session;
import github.microgalaxy.mqtt.broker.config.BrokerConstant;
import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.data.model.NodeMqttPerformanceModel;
import github.microgalaxy.mqtt.broker.event.AbstractMqttEvent;
import github.microgalaxy.mqtt.broker.event.model.MqttEvent;
import github.microgalaxy.mqtt.broker.event.model.MqttEventType;
import github.microgalaxy.mqtt.broker.event.model.TopicSubModel;
import github.microgalaxy.mqtt.broker.event.model.TopicUnSubModel;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Slf4j
@Component
public class ClientEventMonitor<T extends MqttEvent<MqttEventType>> extends AbstractMqttEvent<T> {
    @Autowired
    private BrokerProperties brokerProperties;
    @Resource
    private IgniteCache<String, Session> clientSessionCache;
    @Resource
    private IgniteCache<String, NodeMqttPerformanceModel> nodeMqttPerformanceCache;

    @PostConstruct
    private void init() {
        NodeMqttPerformanceModel cacheMode = nodeMqttPerformanceCache.get(brokerProperties.getBrokerId());
        cacheMode.setClientNumber(clientSessionCache.size());
        nodeMqttPerformanceCache.put(brokerProperties.getBrokerId(), cacheMode);
    }

    @Override
    protected void onConnected(T event) {
        Lock lock = nodeMqttPerformanceCache.lock(MqttEventType.CLIENT_CONNECT.name());
        lock.lock();
        try {
            NodeMqttPerformanceModel cacheMode = nodeMqttPerformanceCache.get(brokerProperties.getBrokerId());
            cacheMode.setClientNumber(cacheMode.getClientNumber() + 1);
            nodeMqttPerformanceCache.put(brokerProperties.getBrokerId(), cacheMode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void onDisConnected(T event) {
        Lock lock = nodeMqttPerformanceCache.lock(MqttEventType.CLIENT_CONNECT.name());
        lock.lock();
        try {
            NodeMqttPerformanceModel cacheMode = nodeMqttPerformanceCache.get(brokerProperties.getBrokerId());
            cacheMode.setClientNumber(cacheMode.getClientNumber() - 1);
            nodeMqttPerformanceCache.put(brokerProperties.getBrokerId(), cacheMode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void onMessageArrived(T event) {
//        MsgArriveModel msgModel = JsonUtils.beanToModel(event, MsgArriveModel.class);
        Lock lock = nodeMqttPerformanceCache.lock(MqttEventType.MSG_ARRIVE.name());
        lock.lock();
        try {
            NodeMqttPerformanceModel cacheMode = nodeMqttPerformanceCache.get(brokerProperties.getBrokerId());
            cacheMode.setMsgInNum(cacheMode.getMsgInNum() + 1);
            nodeMqttPerformanceCache.put(brokerProperties.getBrokerId(), cacheMode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void onMessageDelivered(T event) {
        Lock lock = nodeMqttPerformanceCache.lock(MqttEventType.MSG_ARRIVE.name());
        lock.lock();
        try {
            NodeMqttPerformanceModel cacheMode = nodeMqttPerformanceCache.get(brokerProperties.getBrokerId());
            cacheMode.setMsgOutNum(cacheMode.getMsgOutNum() + 1);
            nodeMqttPerformanceCache.put(brokerProperties.getBrokerId(), cacheMode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void onMessageDiscard(T event) {

    }

    @Override
    protected void onSubTopic(T event) {
        List<TopicSubModel> models = (List<TopicSubModel>) event.getPayload();
        String clientId = models.get(0).getClientId();
        Map<Boolean, List<TopicSubModel>> topicTypeMap = models.stream().collect(Collectors.groupingBy(t ->
                        t.getTopic().startsWith(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX),
                Collectors.toList()));
        Lock lock = nodeMqttPerformanceCache.lock(MqttEventType.CLIENT_CONNECT.name());
        lock.lock();
        try {
            NodeMqttPerformanceModel cacheMode = nodeMqttPerformanceCache.get(brokerProperties.getBrokerId());
            if (!CollectionUtils.isEmpty(topicTypeMap.get(Boolean.FALSE))) {
                topicTypeMap.get(Boolean.FALSE).addAll(Optional.ofNullable(cacheMode.getSubscribeTopicMap()
                        .get(clientId)).orElse(new ArrayList<>(0)));
                cacheMode.getSubscribeTopicMap().put(clientId, topicTypeMap.get(Boolean.FALSE));
            }
            if (!CollectionUtils.isEmpty(topicTypeMap.get(Boolean.TRUE))) {
                topicTypeMap.get(Boolean.TRUE).addAll(Optional.ofNullable(cacheMode.getShareSubscribeTopicMap()
                        .get(clientId)).orElse(new ArrayList<>(0)));
                cacheMode.getShareSubscribeTopicMap().put(clientId, topicTypeMap.get(Boolean.TRUE));
            }
            nodeMqttPerformanceCache.put(brokerProperties.getBrokerId(), cacheMode);
        } finally {
            lock.unlock();
        }
    }

    @Override
    protected void onUnSubTopic(T event) {
        List<TopicUnSubModel> models = (List<TopicUnSubModel>) event.getPayload();
        String clientId = models.get(0).getClientId();
        Map<String, List<TopicUnSubModel>> topicMap = models.stream()
                .collect(Collectors.groupingBy(TopicUnSubModel::getTopic, Collectors.toList()));
        Lock lock = nodeMqttPerformanceCache.lock(MqttEventType.CLIENT_CONNECT.name());
        lock.lock();
        try {
            NodeMqttPerformanceModel cacheMode = nodeMqttPerformanceCache.get(brokerProperties.getBrokerId());
            Optional.ofNullable(cacheMode.getSubscribeTopicMap().get(clientId))
                    .ifPresent(l -> l.removeIf(t -> !ObjectUtils.isEmpty(topicMap.get(t.getTopic()))));
            Optional.ofNullable(cacheMode.getShareSubscribeTopicMap().get(clientId))
                    .ifPresent(l -> l.removeIf(t -> !ObjectUtils.isEmpty(topicMap.get(t.getTopic()))));
            nodeMqttPerformanceCache.put(brokerProperties.getBrokerId(), cacheMode);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }
}
