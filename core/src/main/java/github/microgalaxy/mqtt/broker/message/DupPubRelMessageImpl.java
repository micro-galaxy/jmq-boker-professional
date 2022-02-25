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
    private IgniteCache<String, ConcurrentHashMap<Integer, DupPubRelMessage>> dupPubRelMessageCache;
    @Resource
    private IMessagePacketId messagePacketIdServer;

    @Override
    public void put(String clientId, DupPubRelMessage dupPubRelMessage) {
        ConcurrentHashMap<Integer, DupPubRelMessage> messageMap = dupPubRelMessageCache.containsKey(clientId) ?
                dupPubRelMessageCache.get(clientId) : new ConcurrentHashMap<>();
        messageMap.put(dupPubRelMessage.getMessageId(), dupPubRelMessage);
        dupPubRelMessageCache.put(clientId, messageMap);
    }

    @Override
    public List<DupPubRelMessage> get(String clientId) {
        return dupPubRelMessageCache.containsKey(clientId) ?
                new ArrayList<>(dupPubRelMessageCache.get(clientId).values()) :
                Collections.EMPTY_LIST;
    }

    @Override
    public void remove(String clientId, int messageId) {
        if (!dupPubRelMessageCache.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPubRelMessage> messageMap = dupPubRelMessageCache.get(clientId);
        if (!messageMap.containsKey(messageId)) return;
        messageMap.remove(messageId);
        if (CollectionUtils.isEmpty(messageMap)) {
            dupPubRelMessageCache.remove(clientId);
        } else {
            dupPubRelMessageCache.put(clientId, messageMap);
        }
    }

    @Override
    public void removeClient(String clientId) {
        if (!dupPubRelMessageCache.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPubRelMessage> messageMap = dupPubRelMessageCache.get(clientId);
        messageMap.forEach((k, v) -> messagePacketIdServer.releaseMessageId(v.getMessageId()));
        messageMap.clear();
        dupPubRelMessageCache.remove(clientId);
    }
}
