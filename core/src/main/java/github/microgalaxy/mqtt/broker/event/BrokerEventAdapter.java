package github.microgalaxy.mqtt.broker.event;

import github.microgalaxy.mqtt.broker.event.model.MqttEvent;
import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.DiscoveryEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class BrokerEventAdapter<T> implements IBrokerEvent<T> {
    @Autowired
    private AbstractCacheEvent cacheEvent;
    @Autowired
    private AbstractClusterEvent clusterEvent;
    @Autowired
    private AbstractMqttEvent mqttEvent;

    @Override
    public void onEvent(T event) {
        if(event instanceof CacheEvent && Objects.nonNull(cacheEvent)) cacheEvent.onEvent((CacheEvent)event);
        if(event instanceof DiscoveryEvent && Objects.nonNull(clusterEvent)) clusterEvent.onEvent((DiscoveryEvent)event);
        if(event instanceof MqttEvent && Objects.nonNull(mqttEvent)) mqttEvent.onEvent((MqttEvent)event);
    }
}
