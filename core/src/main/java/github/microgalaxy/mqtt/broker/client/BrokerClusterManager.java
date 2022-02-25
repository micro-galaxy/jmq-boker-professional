package github.microgalaxy.mqtt.broker.client;

import org.apache.ignite.Ignite;
import org.apache.ignite.spi.discovery.DiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.TcpDiscoveryIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.spi.discovery.tcp.ipfinder.vm.TcpDiscoveryVmIpFinder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class BrokerClusterManager implements IBrokerCluster {
    @Resource
    private Ignite ignite;

    @Override
    public void joinClusterIp(String staticIp) {
        TcpDiscoverySpi tcpDiscoverySpi = (TcpDiscoverySpi) ignite.configuration().getDiscoverySpi();
        TcpDiscoveryMulticastIpFinder baseFinder = (TcpDiscoveryMulticastIpFinder) tcpDiscoverySpi.getIpFinder();
        baseFinder.setAddresses(Arrays.asList(staticIp));
        tcpDiscoverySpi.setIpFinder(baseFinder);
        tcpDiscoverySpi.disconnect();
        tcpDiscoverySpi.clientReconnect();
    }

    @Override
    public void joinClusterMulticastIp(String multicastIp) {
        TcpDiscoverySpi tcpDiscoverySpi = (TcpDiscoverySpi) ignite.configuration().getDiscoverySpi();
        TcpDiscoveryMulticastIpFinder baseFinder = (TcpDiscoveryMulticastIpFinder) tcpDiscoverySpi.getIpFinder();
        baseFinder.setMulticastGroup(multicastIp);
        tcpDiscoverySpi.setIpFinder(baseFinder);
        tcpDiscoverySpi.disconnect();
        tcpDiscoverySpi.clientReconnect();
    }

    @Override
    public void leftCluster() {
        ignite.configuration().getDiscoverySpi().disconnect();
    }

    @Override
    public void reconnectCluster() {
        TcpDiscoverySpi tcpDiscoverySpi = (TcpDiscoverySpi) ignite.configuration().getDiscoverySpi();
        tcpDiscoverySpi.clientReconnect();
    }
}
