package github.microgalaxy.mqtt.broker.client;

import github.microgalaxy.mqtt.broker.config.BrokerConstant;
import github.microgalaxy.mqtt.broker.message.RetainMessage;
import github.microgalaxy.mqtt.broker.util.TopicUtils;
import org.apache.ignite.IgniteCache;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class SubscribeStoreImpl implements ISubscribeStore {
    @Resource
    private IgniteCache<String, ConcurrentHashMap<String, Subscribe>> clientSubscribeCatch;
    @Resource
    private IgniteCache<String, ConcurrentHashMap<String, Subscribe>> clientShareSubscribeCatch;

    @Override
    public void put(String topic, Subscribe subscribe) {
        IgniteCache<String, ConcurrentHashMap<String, Subscribe>> topicSubscribeMap =
                topic.startsWith(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX) ?
                        clientShareSubscribeCatch : clientSubscribeCatch;
        ConcurrentHashMap<String, Subscribe> subscribeMap = topicSubscribeMap.containsKey(topic) ?
                topicSubscribeMap.get(topic) : new ConcurrentHashMap<>();
        subscribeMap.put(subscribe.getClientId(), subscribe);
        topicSubscribeMap.put(topic, subscribeMap);
    }

    @Override
    public void remove(String topic, String clientId) {
        IgniteCache<String, ConcurrentHashMap<String, Subscribe>> topicSubscribeMap =
                topic.startsWith(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX) ?
                        clientShareSubscribeCatch : clientSubscribeCatch;
        if (!topicSubscribeMap.containsKey(topic)) return;
        ConcurrentHashMap<String, Subscribe> subscribeMap = topicSubscribeMap.get(topic);
        if (!subscribeMap.containsKey(clientId)) return;
        subscribeMap.remove(clientId);
        if (CollectionUtils.isEmpty(subscribeMap)) {
            topicSubscribeMap.remove(topic);
        } else {
            topicSubscribeMap.put(topic, subscribeMap);
        }
    }

    @Override
    public void removeClient(String clientId) {
        clientSubscribeCatch.forEach(en -> {
            ConcurrentHashMap<String, Subscribe> subscribeMap = en.getValue();
            if (!subscribeMap.containsKey(clientId)) return;
            subscribeMap.remove(clientId);
            if (CollectionUtils.isEmpty(subscribeMap)) {
                subscribeMap.remove(en.getKey());
            } else {
                clientSubscribeCatch.put(en.getKey(), subscribeMap);
            }
        });
        clientShareSubscribeCatch.forEach(en -> {
            ConcurrentHashMap<String, Subscribe> subscribeMap = en.getValue();
            if (!subscribeMap.containsKey(clientId)) return;
            subscribeMap.remove(clientId);
            if (CollectionUtils.isEmpty(subscribeMap)) {
                subscribeMap.remove(en.getKey());
            } else {
                clientShareSubscribeCatch.put(en.getKey(), subscribeMap);
            }
        });
    }

    @Override
    public Collection<Subscribe> matchTopic(String publishTopic) {
        List<Subscribe> matchedTopics = new ArrayList<>();
        clientSubscribeCatch.forEach(s -> {
            boolean matched = TopicUtils.matchingTopic(s.getKey(), publishTopic);
            if (matched) matchedTopics.addAll(s.getValue().values());
        });
        return matchedTopics;
    }

    @Override
    public Collection<Subscribe> matchShareTopic(String publishTopic) {
        List<Subscribe> matchedTopics = new ArrayList<>();
        clientShareSubscribeCatch.forEach(s -> {
            boolean matched = TopicUtils.matchingTopic(s.getKey(), publishTopic);
            if (matched) matchedTopics.addAll(s.getValue().values());
        });
        return matchedTopics;
    }

    @Override
    public void upNode(String clientId, String brokerId) {
        clientSubscribeCatch.forEach(en -> {
            Subscribe subscribe = en.getValue().get(clientId);
            if (!ObjectUtils.isEmpty(subscribe)) subscribe.setJmqId(brokerId);
        });
        clientShareSubscribeCatch.forEach(en -> {
            Subscribe subscribe = en.getValue().get(clientId);
            if (!ObjectUtils.isEmpty(subscribe)) subscribe.setJmqId(brokerId);
        });
    }

    @Override
    public boolean repeatSubscribe(String clientId, String topic) {
        Object subscribe = Optional.ofNullable(clientSubscribeCatch.get(topic))
                .orElse(new ConcurrentHashMap<>(0)).get(clientId);
        Object shareSubscribe = Optional.ofNullable(clientShareSubscribeCatch.get(topic))
                .orElse(new ConcurrentHashMap<>(0)).get(clientId);
        return !ObjectUtils.isEmpty(subscribe) || !ObjectUtils.isEmpty(shareSubscribe);
    }
}
