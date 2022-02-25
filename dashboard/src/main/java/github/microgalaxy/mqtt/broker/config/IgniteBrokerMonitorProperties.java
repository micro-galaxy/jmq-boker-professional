package github.microgalaxy.mqtt.broker.config;

import github.microgalaxy.mqtt.broker.data.model.NodeMetaModel;
import github.microgalaxy.mqtt.broker.data.model.NodeMqttPerformanceModel;
import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.user.PermissionLevel;
import github.microgalaxy.mqtt.broker.user.User;
import github.microgalaxy.mqtt.broker.util.TopicUtils;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Configuration
public class IgniteBrokerMonitorProperties {
    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private HttpServerProperties httpServerProperties;
    @Autowired
    private Ignite ignite;

    @Bean
    public IgniteCache nodeClientCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(IgniteAutoConfig.DATA_REGION_PERSISTENCE)
                .setName("nodeClientCache");
        return ignite.getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache nodeClusterCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(IgniteAutoConfig.DATA_REGION_PERSISTENCE)
                .setName("nodeClusterCache");
        return ignite.getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache nodeMetaCache() {
        CacheConfiguration<String, NodeMetaModel> cfg = new CacheConfiguration<String, NodeMetaModel>()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(IgniteAutoConfig.DATA_REGION_PERSISTENCE)
                .setName("nodeMetaCache");
        IgniteCache<String, NodeMetaModel> cache = ignite.getOrCreateCache(cfg);
        String brokerId = brokerProperties.getBrokerId();
        if (ObjectUtils.isEmpty(cache.get(brokerId))) {
            NodeMetaModel node = new NodeMetaModel();
            node.setBrokerId(brokerId);
            node.setIp(TopicUtils.getMacIp());
            node.setMqttPort(brokerProperties.getMqttPort());
            node.setMqttWsPort(brokerProperties.getMqttWsPort());
            node.setWsPath(brokerProperties.getWsPath());
            node.setHttpPort(httpServerProperties.getPort());
            node.setJoinTime(new Date());
            cache.put(brokerId, node);
        }
        return cache;
    }


    @Bean
    public IgniteCache nodeMqttPerformanceCache() {
        CacheConfiguration<String, NodeMqttPerformanceModel> cfg = new CacheConfiguration<String, NodeMqttPerformanceModel>()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(IgniteAutoConfig.DATA_REGION_PERSISTENCE)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setName("nodeMqttPerformanceCache")
                .setIndexedTypes(String.class, NodeMqttPerformanceModel.class);
        IgniteCache<String, NodeMqttPerformanceModel> cache = ignite.getOrCreateCache(cfg);
        if (ObjectUtils.isEmpty(cache.get(brokerProperties.getBrokerId()))) {
            NodeMqttPerformanceModel model = new NodeMqttPerformanceModel();
            model.setBrokerId(brokerProperties.getBrokerId());
            model.setClientNumber(0);
            model.setSubscribeTopicMap(new HashMap<>(8));
            model.setRetainMessageNumber(0);
            model.setShareSubscribeTopicMap(new HashMap<>(8));
            model.setMsgInNum(0L);
            model.setMsgOutNum(0L);
            model.setMsgDiscardNum(0L);
            model.setReceiveFlowTotal(0L);
            model.setSendFlowTotal(0L);
            cache.put(model.getBrokerId(), model);
        }
        return cache;
    }

    @Bean
    public IgniteCache nodePerformanceCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(IgniteAutoConfig.DATA_REGION_NOT_PERSISTENCE)
                .setName("nodePerformanceCache");
        return ignite.getOrCreateCache(cfg);
    }


    @Bean
    public IgniteCache topicSubscribeCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(IgniteAutoConfig.DATA_REGION_NOT_PERSISTENCE)
                .setName("topicSubscribeCache");
        return ignite.getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache<String, User> dashboardUser() {
        CacheConfiguration<String, User> cfg = new CacheConfiguration<String, User>()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(IgniteAutoConfig.DATA_REGION_PERSISTENCE)
                .setName("dashboardUser")
                .setIndexedTypes(String.class, User.class);
        IgniteCache<String, User> userCache = ignite.getOrCreateCache(cfg);
        if (ObjectUtils.isEmpty(userCache.get(PermissionLevel.ADMIN.name().toLowerCase()))) {
            User user = new User();
            user.setUsername(PermissionLevel.ADMIN.name().toLowerCase());
            user.setPassword(PermissionLevel.ADMIN.name().toLowerCase());
            user.setLevel(PermissionLevel.ADMIN);
            user.setDisable(false);
            user.setCreateTime(new Date());
            userCache.put(PermissionLevel.ADMIN.name().toLowerCase(), user);
        }
        return userCache;
    }
}
