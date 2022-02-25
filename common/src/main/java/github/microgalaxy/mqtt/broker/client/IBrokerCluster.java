package github.microgalaxy.mqtt.broker.client;

/**
 * 提供本机节点集群控制接口
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public interface IBrokerCluster {
    /**
     * 通过静态IP加入集群
     *
     * @param staticIp
     */
    void joinClusterIp(String staticIp);

    /**
     * 通过组播IP加入集群
     *
     * @param multicastIp
     */
    void joinClusterMulticastIp(String multicastIp);

    /**
     * 离开集群
     */
    void leftCluster();

    /**
     * 集群重连
     */
    void reconnectCluster();
}
