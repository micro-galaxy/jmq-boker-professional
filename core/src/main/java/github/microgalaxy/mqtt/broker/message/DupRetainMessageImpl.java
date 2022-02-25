package github.microgalaxy.mqtt.broker.message;

import github.microgalaxy.mqtt.broker.util.TopicUtils;
import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class DupRetainMessageImpl implements IDupRetainMessage {
    @Resource
    private IgniteCache<String, RetainMessage> retainMessageCache;

    @Override
    public void put(String topic, RetainMessage retainMessage) {
        retainMessageCache.put(topic, retainMessage);
    }

    @Override
    public RetainMessage get(String topic) {
        return retainMessageCache.get(topic);
    }

    @Override
    public List<RetainMessage> match(String subscribeTopic) {
        List<RetainMessage> matchedTopics = new ArrayList<>();
        retainMessageCache.forEach(en -> {
            boolean matched = TopicUtils.matchingTopic(subscribeTopic, en.getKey());
            if (matched) matchedTopics.add(en.getValue());
        });
        return matchedTopics;
    }

    @Override
    public void remove(String topic) {
        retainMessageCache.remove(topic);
    }
}
