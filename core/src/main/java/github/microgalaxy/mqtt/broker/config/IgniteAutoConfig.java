package github.microgalaxy.mqtt.broker.config;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.IgniteMessaging;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.cache.CacheMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.configuration.DataRegionConfiguration;
import org.apache.ignite.configuration.DataStorageConfiguration;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.logger.slf4j.Slf4jLogger;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.Arrays;

/**
 * apache ignite配置
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Configuration
public class IgniteAutoConfig {
    private static final String DATA_REGION_PERSISTENCE = "data-region-persistence";
    private static final String DATA_REGION_NOT_PERSISTENCE = "data-region-not-persistence";
    private static final String FILE_PERSISTENCE_DIR = "persistence";

    @Autowired
    private BrokerProperties brokerProperties;
    @Autowired
    private BrokerCacheProperties brokerCacheProperties;

    @Bean
    public Ignite ignite() {
        IgniteConfiguration igniteConfiguration = new IgniteConfiguration();
        //node name
        igniteConfiguration.setIgniteInstanceName(brokerProperties.getBrokerId());
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
                .setName(DATA_REGION_PERSISTENCE);
        //store config
        String persistencePath = new File("").getAbsolutePath() + File.separator + FILE_PERSISTENCE_DIR;
        DataStorageConfiguration dataStorageConfiguration = new DataStorageConfiguration()
                .setDefaultDataRegionConfiguration(notPersistence)
                .setDataRegionConfigurations(persistence)
                .setWalPath(persistencePath)
                .setWalArchivePath(persistencePath)
                .setStoragePath(persistencePath);
        igniteConfiguration.setDataStorageConfiguration(dataStorageConfiguration);
        //Multicast clusters or ip clusters spi
        TcpDiscoverySpi tcpDiscoverySpi = new TcpDiscoverySpi();
        TcpDiscoveryVmIpFinder ipFinder = new TcpDiscoveryMulticastIpFinder()
                .setMulticastGroup(brokerProperties.getClusterMulticastGroupIp())
                .setAddresses(Arrays.asList(brokerProperties.getClusterStaticIps()));
        tcpDiscoverySpi.setIpFinder(ipFinder);
        igniteConfiguration.setDiscoverySpi(tcpDiscoverySpi);

        return Ignition.start(igniteConfiguration);
    }

    @Bean
    public IgniteCache messageIdCatch() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setDataRegionName(DATA_REGION_NOT_PERSISTENCE)
                .setCacheMode(CacheMode.PARTITIONED)
                .setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL)
                .setName("messageIdCatch");
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache dupPublishMessageCatch() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.PARTITIONED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName("dupPublishMessageCatch");
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache dupPubRelMessageCatch() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.PARTITIONED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName("dupPubRelMessageCatch");
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache retainMessageCatch() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.PARTITIONED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName("retainMessageCatch");
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache clientSubscribeCatch() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.PARTITIONED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName("clientSubscribeCatch");
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteCache clientShareSubscribeCatch() {
        CacheConfiguration cfg = new CacheConfiguration()
                .setCacheMode(CacheMode.PARTITIONED)
                .setDataRegionName(DATA_REGION_PERSISTENCE)
                .setName("clientShareSubscribeCatch");
        return ignite().getOrCreateCache(cfg);
    }

    @Bean
    public IgniteMessaging igniteMessaging() {
        return ignite().message(ignite().cluster().forRemotes());
    }
}
