package github.microgalaxy.mqtt.broker.event;

/**
 * Mqtt broker 集群事件、客户端消息等事件
 * <p>
 * 集群事件：加入、离开、断开、重连事件
 * 客户端消息：客户端连接、客户端断开、客户端消息抵达、服务端消息发送
 * 等
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public interface IBrokerEvent<T> {

    /**
     * 集群节点事件
     *
     * @param event
     */
    void  onEvent(T event);
}
