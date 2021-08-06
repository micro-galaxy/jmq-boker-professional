package github.microgalaxy.mqtt.broker.message;

import org.apache.ignite.IgniteCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class DupPubRelMessageImpl implements IDupPubRelMessage {
    @Resource
    private IgniteCache<String, ConcurrentHashMap<Integer, DupPubRelMessage>> dupPubRelMessageCatch;
    @Resource
    private IMessagePacketId messagePacketIdServer;

    @Override
    public void put(String clientId, DupPubRelMessage dupPubRelMessage) {
        ConcurrentHashMap<Integer, DupPubRelMessage> messageMap = dupPubRelMessageCatch.containsKey(clientId) ?
                dupPubRelMessageCatch.get(clientId) : new ConcurrentHashMap<>();
        messageMap.put(dupPubRelMessage.getMessageId(), dupPubRelMessage);
        dupPubRelMessageCatch.put(clientId, messageMap);
    }

    @Override
    public List<DupPubRelMessage> get(String clientId) {
        return dupPubRelMessageCatch.containsKey(clientId) ?
                new ArrayList<>(dupPubRelMessageCatch.get(clientId).values()) :
                Collections.EMPTY_LIST;
    }

    @Override
    public void remove(String clientId, int messageId) {
        if (!dupPubRelMessageCatch.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPubRelMessage> messageMap = dupPubRelMessageCatch.get(clientId);
        if (!messageMap.containsKey(messageId)) return;
        messageMap.remove(messageId);
        if (CollectionUtils.isEmpty(messageMap)) {
            dupPubRelMessageCatch.remove(clientId);
        } else {
            dupPubRelMessageCatch.put(clientId, messageMap);
        }
    }

    @Override
    public void removeClient(String clientId) {
        if (!dupPubRelMessageCatch.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPubRelMessage> messageMap = dupPubRelMessageCatch.get(clientId);
        messageMap.forEach((k, v) -> messagePacketIdServer.releaseMessageId(v.getMessageId()));
        messageMap.clear();
        dupPubRelMessageCatch.remove(clientId);
    }
}
