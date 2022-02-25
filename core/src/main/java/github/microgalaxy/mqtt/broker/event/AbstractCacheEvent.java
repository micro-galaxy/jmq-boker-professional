package github.microgalaxy.mqtt.broker.event;

import org.apache.ignite.events.CacheEvent;
import org.apache.ignite.events.EventType;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Mqtt 缓存事件
 * <p>
 * 写入、移除事件
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public abstract class AbstractCacheEvent<T extends CacheEvent> implements IBrokerEvent<T> {
    private final Map<Integer, Consumer<T>> evtMap = new HashMap<>();

    protected AbstractCacheEvent() {
        evtMap.put(EventType.EVT_CACHE_OBJECT_PUT, this::onCachePut);
        evtMap.put(EventType.EVT_CACHE_OBJECT_REMOVED, this::onCacheRemove);
    }

    @Override
    public void onEvent(T event) {
        evtMap.get(event.type()).accept(event);
    }

    /**
     * 写入缓存
     *
     * @param event
     */
    protected abstract void onCachePut(T event);

    /**
     * 缓存删除
     *
     * @param event
     */
    protected abstract void onCacheRemove(T event);

}
