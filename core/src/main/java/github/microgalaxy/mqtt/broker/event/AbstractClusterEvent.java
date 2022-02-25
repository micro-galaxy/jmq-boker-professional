package github.microgalaxy.mqtt.broker.event;

import org.apache.ignite.events.DiscoveryEvent;
import org.apache.ignite.events.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Mqtt broker 集群事件
 * <p>
 * 加入、离开、断开、重连事件
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public abstract class AbstractClusterEvent<T extends DiscoveryEvent> implements IBrokerEvent<T> {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Map<Integer, Consumer<T>> evtMap = new HashMap<>();

    protected AbstractClusterEvent() {
        evtMap.put(EventType.EVT_NODE_JOINED, this::onNodeJoined);
        evtMap.put(EventType.EVT_NODE_LEFT, this::onNodeLeft);
        evtMap.put(EventType.EVT_NODE_FAILED, this::onNodeFailed);
        evtMap.put(EventType.EVT_CLIENT_NODE_DISCONNECTED, this::onClientNodeDisconnected);
        evtMap.put(EventType.EVT_CLIENT_NODE_RECONNECTED, this::onClientNodeReconnected);
    }

    @Override
    public void onEvent(T event) {
        evtMap.get(event.type()).accept(event);
    }

    /**
     * 节点加入集群
     *
     * @param event
     */
    protected abstract void onNodeJoined(T event);

    /**
     * 节点离开集群
     *
     * @param event
     */
    protected abstract void onNodeLeft(T event);

    /**
     * 集群检测到某节点非正常离开集群
     *
     * @param event
     */
    protected abstract void onNodeFailed(T event);

    /**
     * 客户端断开了与集群的连接
     *
     * @param event
     */
    protected abstract void onClientNodeDisconnected(T event);

    /**
     * 客户端节点与集群重连
     *
     * @param event
     */
    protected abstract void onClientNodeReconnected(T event);
}
