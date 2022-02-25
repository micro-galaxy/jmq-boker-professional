package github.microgalaxy.mqtt.broker.data.model;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class LRU<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRU(int initialCapacity) {
        super(initialCapacity);
        capacity = initialCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
