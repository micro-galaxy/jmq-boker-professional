package github.microgalaxy.mqtt.broker.monitor.performance;

import github.microgalaxy.mqtt.broker.config.BrokerProperties;
import github.microgalaxy.mqtt.broker.data.model.NodeClusterModel;
import github.microgalaxy.mqtt.broker.data.model.NodeMetaModel;
import github.microgalaxy.mqtt.broker.event.AbstractCacheEvent;
import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.server.http.util.HttpUtils;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.events.CacheEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class BrokerCacheMonitor<T extends CacheEvent> extends AbstractCacheEvent<T> {
    @Resource
    private IgniteCache<String, NodeMetaModel> nodeMetaCache;
    @Resource
    private IgniteCache<String, NodeClusterModel> nodeClusterCache;
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private HttpServerProperties httpServerProperties;


    @Override
    protected void onCachePut(T event) {
        System.out.println(event);
    }

    @Override
    protected void onCacheRemove(T event) {

    }
}
