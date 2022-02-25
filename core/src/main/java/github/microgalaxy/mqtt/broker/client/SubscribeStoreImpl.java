package github.microgalaxy.mqtt.broker.client;

import github.microgalaxy.mqtt.broker.config.BrokerConstant;
import github.microgalaxy.mqtt.broker.event.IBrokerEvent;
import github.microgalaxy.mqtt.broker.util.MqttEventUtils;
import github.microgalaxy.mqtt.broker.util.TopicUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Slf4j
@Component
public class SubscribeStoreImpl implements ISubscribeStore {
    @Autowired
    @Lazy
    private IBrokerEvent brokerEventAdapter;
    @Autowired
    private ISessionStore sessionServer;
    @Resource
    private IgniteCache<String, ConcurrentHashMap<String, Subscribe>> clientSubscribeCache;
    @Resource
    private IgniteCache<String, ConcurrentHashMap<String, Subscribe>> clientShareSubscribeCache;

    @Override
    public Collection<Subscribe> put(Collection<Subscribe> subscribes) {
        Map<Boolean, List<Subscribe>> shareMap = subscribes.stream()
                .collect(Collectors.groupingBy(s ->
                                s.getTopic().startsWith(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX),
                        Collectors.toList()));
        return shareMap.entrySet().stream()
                .map(en -> {
                    IgniteCache<String, ConcurrentHashMap<String, Subscribe>> topicSubscribeMap = en.getKey() ?
                            clientShareSubscribeCache : clientSubscribeCache;
                    Lock lock = topicSubscribeMap.lock(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX);
                    lock.lock();
                    try {
                        return en.getValue().stream().filter(t -> {
                            ConcurrentHashMap<String, Subscribe> subscribeMap = topicSubscribeMap.containsKey(t.getTopic()) ?
                                    topicSubscribeMap.get(t.getTopic()) : new ConcurrentHashMap<>();
                            boolean notExist = Objects.isNull(subscribeMap.get(t.getClientId()));
                            subscribeMap.put(t.getClientId(), t);
                            topicSubscribeMap.put(t.getTopic(), subscribeMap);
                            return notExist;
                        }).collect(Collectors.toList());
                    } finally {
                        lock.unlock();
                    }
                }).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    @Override
    public Collection<String> remove(String clientId, Collection<String> topic) {
        Map<Boolean, List<String>> shareMap = topic.stream()
                .collect(Collectors.groupingBy(t ->
                                t.startsWith(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX),
                        Collectors.toList()));
        return shareMap.entrySet().stream()
                .map(en -> {
                    IgniteCache<String, ConcurrentHashMap<String, Subscribe>> topicSubscribeMap = en.getKey() ?
                            clientShareSubscribeCache : clientSubscribeCache;
                    Lock lock = topicSubscribeMap.lock(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX);
                    lock.lock();
                    try {
                        return en.getValue().stream().filter(t -> {
                            if (!topicSubscribeMap.containsKey(t)) return false;
                            ConcurrentHashMap<String, Subscribe> subscribeMap = topicSubscribeMap.get(t);
                            if (!subscribeMap.containsKey(clientId)) return false;
                            subscribeMap.remove(clientId);
                            if (CollectionUtils.isEmpty(subscribeMap)) {
                                topicSubscribeMap.remove(t);
                            } else {
                                topicSubscribeMap.put(t, subscribeMap);
                            }
                            return true;
                        }).collect(Collectors.toList());
                    } catch (Exception e){
                        log.error(e.getMessage(),e);
                        return null;
                    }
                    finally {
                        lock.unlock();
                    }
                }).collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    @Override
    public void removeClient(String clientId) {
        List<String> topics = new ArrayList<>();
        Lock lock = clientSubscribeCache.lock(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX);
        lock.lock();
        try {
            clientSubscribeCache.forEach(en -> {
                ConcurrentHashMap<String, Subscribe> subscribeMap = en.getValue();
                if (!subscribeMap.containsKey(clientId)) return;
                topics.add(subscribeMap.get(clientId).getTopic());
                subscribeMap.remove(clientId);
                if (CollectionUtils.isEmpty(subscribeMap)) {
                    subscribeMap.remove(en.getKey());
                }
                clientSubscribeCache.put(en.getKey(), subscribeMap);
            });
        } finally {
            lock.unlock();
        }

        Lock sLock = clientShareSubscribeCache.lock(BrokerConstant.ShareSubscribe.SUBSCRIBE_SHARE_PREFIX);
        sLock.lock();
        try {
            clientShareSubscribeCache.forEach(en -> {
                ConcurrentHashMap<String, Subscribe> subscribeMap = en.getValue();
                if (!subscribeMap.containsKey(clientId)) return;
                topics.add(subscribeMap.get(clientId).getTopic());
                subscribeMap.remove(clientId);
                if (CollectionUtils.isEmpty(subscribeMap)) {
                    subscribeMap.remove(en.getKey());
                }
                clientShareSubscribeCache.put(en.getKey(), subscribeMap);
            });
        } finally {
            sLock.unlock();
        }
        MqttEventUtils.onTopicUnSubEvent(clientId, topics, brokerEventAdapter, sessionServer);
    }

    @Override
    public Collection<Subscribe> matchTopic(String publishTopic) {
        List<Subscribe> matchedTopics = new ArrayList<>();
        clientSubscribeCache.forEach(s -> {
            boolean matched = TopicUtils.matchingTopic(s.getKey(), publishTopic);
            if (matched) matchedTopics.addAll(s.getValue().values());
        });
        return matchedTopics;
    }

    @Override
    public Collection<Subscribe> matchShareTopic(String publishTopic) {
        List<Subscribe> matchedTopics = new ArrayList<>();
        clientShareSubscribeCache.forEach(s -> {
            boolean matched = TopicUtils.matchingShareTopic(s.getKey(), publishTopic);
            if (matched) matchedTopics.addAll(s.getValue().values());
        });
        return matchedTopics;
    }

    @Override
    public void upNode(String clientId, String brokerId) {
        clientSubscribeCache.forEach(en -> {
            Subscribe subscribe = en.getValue().get(clientId);
            if (!ObjectUtils.isEmpty(subscribe)) subscribe.setJmqId(brokerId);
        });
        clientShareSubscribeCache.forEach(en -> {
            Subscribe subscribe = en.getValue().get(clientId);
            if (!ObjectUtils.isEmpty(subscribe)) subscribe.setJmqId(brokerId);
        });
    }

    @Override
    public boolean repeatSubscribe(String clientId, String topic) {
        Subscribe subscribe = Optional.ofNullable(clientSubscribeCache.get(topic))
                .orElse(new ConcurrentHashMap<>(0)).get(clientId);
        Subscribe shareSubscribe = Optional.ofNullable(clientShareSubscribeCache.get(topic))
                .orElse(new ConcurrentHashMap<>(0)).get(clientId);
        return !ObjectUtils.isEmpty(subscribe) || !ObjectUtils.isEmpty(shareSubscribe);
    }
}
