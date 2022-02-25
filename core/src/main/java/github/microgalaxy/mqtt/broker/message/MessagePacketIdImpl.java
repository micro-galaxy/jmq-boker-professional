package github.microgalaxy.mqtt.broker.message;

import github.microgalaxy.mqtt.broker.handler.MqttException;
import io.netty.handler.codec.mqtt.MqttConnectReturnCode;
import io.netty.handler.codec.mqtt.MqttVersion;
import org.apache.ignite.IgniteCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.locks.Lock;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class MessagePacketIdImpl implements IMessagePacketId {
    private final Logger log            =   LoggerFactory.getLogger(this.getClass());
    private final int MIN_PACKET_ID     =   1;
    private final int MAX_PACKET_ID     =   1 << 16 - 1;
    private int currentPacketId         =   MIN_PACKET_ID;
    @Resource
    private IgniteCache<Integer, Integer> messageIdCache;

    @Override
    public int nextMessageId(MqttVersion mqttVersion) {
        Lock lock = messageIdCache.lock(0);
        lock.lock();
        try {
            for (; ; ) {
                if (!messageIdCache.containsKey(currentPacketId)) {
                    messageIdCache.put(currentPacketId, currentPacketId);
                    return currentPacketId;
                }
                currentPacketId++;
                if (currentPacketId > MAX_PACKET_ID) currentPacketId = MIN_PACKET_ID;
            }
        } catch (Exception e) {
            log.error("Generate message packetId error:{}", e.getMessage(), e);
            throw new MqttException(MqttVersion.MQTT_5.protocolLevel() > mqttVersion.protocolLevel() ?
                    (int) MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_UNAVAILABLE.byteValue() :
                    (int) MqttConnectReturnCode.CONNECTION_REFUSED_SERVER_BUSY.byteValue(), false, e.getMessage());
        } finally {
            lock.unlock();
        }
    }

    @Override
    public synchronized void releaseMessageId(int messageId) {
        messageIdCache.remove(messageId);
    }
}
