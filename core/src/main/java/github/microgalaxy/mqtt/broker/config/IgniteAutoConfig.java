package github.microgalaxy.mqtt.broker.config;

import github.microgalaxy.mqtt.broker.client.Session;
import github.microgalaxy.mqtt.broker.event.IBrokerEvent;
import github.microgalaxy.mqtt.broker.security.BrokerSecurity;
import github.microgalaxy.mqtt.broker.util.TopicUtils;
import org.apache.ignite.*;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.cluster.ClusterState;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.events.EventType;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * apache ignite配置
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Configuration
public class IgniteAutoConfig {
    public static final String DATA_REGION_PERSISTENCE = "data-region-persistence";
    public static final String DATA_REGION_NOT_PERSISTENCE = "data-region-not-persistence";
    private static final String FILE_PERSISTENCE_DIR = "persistence";
    private int[] clusterEventTypes = {EventType.EVT_NODE_JOINED, EventType.EVT_NODE_LEFT,
            EventType.EVT_NODE_FAILED, EventType.EVT_CLIENT_NODE_DISCONNECTED, EventType.EVT_CLIENT_NODE_RECONNECTED,
            EventType.EVT_CACHE_OBJECT_PUT, EventType.EVT_CACHE_OBJECT_REMOVED};

    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private BrokerCacheProperties brokerCacheProperties;
    @Autowired
    private BrokerSecurity brokerSecurity;
    @Autowired
    @Lazy
    private IBrokerEvent brokerEventAdapter;

    @Bean
    public Ignite ignite() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        //node attribute
        igniteConfiguration.setIgniteInstanceName(brokerProperties.getBrokerId());
        Map<String, Object> userAttributes = new HashMap<>(2);
        userAttributes.put(BrokerConstant.BROKER_KEY, brokerProperties.getBrokerId());
        userAttributes.put(BrokerConstant.BROKER_MQTT_PORT_KEY, brokerProperties.getMqttPort());
        userAttributes.put(BrokerConstant.BROKER_CLUSTER_KEY, brokerProperties.getClusterKey());
        userAttributes.put(BrokerConstant.BROKER_NODE_IP_KEY, TopicUtils.getMacIp());
        igniteConfiguration.setUserAttributes(userAttributes);
        //log
        igniteConfiguration.setGridLogger(new Slf4jLogger(LoggerFactory.getLogger("org.apache.ignite")));
        //data not persistence
        DataRegionConfiguration notPersistence = new DataRegionConfiguration()
                .setInitialSize(brokerCacheProperties.getMemoryPersistenceInitialSize() * 1024 * 1024)
                .setMaxSize(brokerCacheProperties.getMemoryPersistenceMaxSize() * 1024 * 1024)
                .setName(DATA_REGION_NOT_PERSISTENCE);
        //data persistence
        DataRegionConfiguration persistence = new DataRegionConfiguration()
                .setInitialSize(brokerCacheProperties.getFilePersistenceInitialSize() * 1024 * 1024)
                .setMaxSize(brokerCacheProperties.getFilePersistenceMaxSize() * 1024 * 1024)
                .setName(DATA_REGION_PERSISTENCE)
                .setPersistenceEnabled(true);
        //store config
        String absPath = new File("").getAbsolutePath() + File.separator + "jmqConf";
        String persistencePath = absPath + File.separator + FILE_PERSISTENCE_DIR + File.separator + brokerProperties.getBrokerId();
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration()
                .setDefaultDataRegionConfiguration(notPersistence)
                .setDataRegionConfigurations(persistence)
                .setWalPath(persistencePath)
                .setWalArchivePath(persistencePath)
                .setStoragePath(persistencePath);
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        igniteConfiguration.setWorkDirectory(absPath + File.separator + "work");
        //ignite cluster event
        igniteConfiguration.setIncludeEventTypes(clusterEventTypes);
        //Multicast clusters or ip clusters spi
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder()
                .setMulticastGroup(brokerProperties.getClusterMulticastGroupIp())
                .setAddresses(Arrays.asList(brokerProperties.getClusterStaticIps()));
        tcpDiscoverySpi.setIpFinder(ipFinder);
        tcpDiscoverySpi.setName(brokerProperties.getBrokerId());
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);
        //Cluster、API security
        igniteConfiguration.setPluginProviders(brokerSecurity);

        Ignite ignite = Ignition.start(igniteConfiguration);
        ignite.cluster().state(ClusterState.ACTIVE);
        ignite.cluster().baselineAutoAdjustEnabled(true);
        ignite.cluster().baselineAutoAdjustTimeout(3000);
        return ignite;
    }

    @Bean
    public IgniteCache messageIdCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setDataRegionName(DATA_REGION_NOT_PERSISTENCE)
                .setCacheMode(CacheMode.REPLICATED)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setName(BrokerConstant.CacheKey.BROKER_MESSAGE_ID_CATCH);
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache<String,Session> clientSessionCache() {
        CacheConfiguration<String,Session> cfg = new CacheConfiguration<String,Session>()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(DATA_REGION_NOT_PERSISTENCE)
                .setName(BrokerConstant.CacheKey.BROKER_CLIENT_SESSION_CATCH)
                .setIndexedTypes(String.class, Session.class);
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache dupPublishMessageCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName(BrokerConstant.CacheKey.BROKER_DUP_PUBLISH_MESSAGE_CATCH);
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache dupPubRelMessageCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName(BrokerConstant.CacheKey.BROKER_DUP_PUB_REL_MESSAGE_CATCH);
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache retainMessageCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName(BrokerConstant.CacheKey.BROKER_RETAIN_MESSAGE_CATCH);
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache clientSubscribeCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setName(BrokerConstant.CacheKey.BROKER_CLIENT_SUBSCRIBE_CATCH);
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache clientShareSubscribeCache() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.REPLICATED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setName(BrokerConstant.CacheKey.BROKER_CLIENT_SHARE_SUBSCRIBE_CATCH);
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteMessaging igniteMessaging() {
        return ignite().message(ignite().cluster().forRemotes());
    }

    @PostConstruct
    public void igniteClusterEvent() {
        brokerProperties.addOnBeanReadyEvent((type) -> {
            IgniteEvents events = ignite().events(ignite().cluster().forLocal());
            events.localListen(event -> {
                brokerEventAdapter.onEvent(event);
                return true;
            }, clusterEventTypes);
        });
    }
}
