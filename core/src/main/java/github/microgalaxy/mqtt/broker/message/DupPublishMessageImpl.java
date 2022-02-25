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
public class DupPublishMessageImpl implements IDupPublishMessage {
    @Resource
    private IgniteCache<String, ConcurrentHashMap<Integer, DupPublishMessage>> dupPublishMessageCache;
    @Resource
    private IMessagePacketId messagePacketIdServer;

    @Override
    public void put(String clientId, DupPublishMessage dupPublishMessage) {
        ConcurrentHashMap<Integer, DupPublishMessage> messageMap = dupPublishMessageCache.containsKey(clientId) ?
                dupPublishMessageCache.get(clientId) : new ConcurrentHashMap<>();
        messageMap.put(dupPublishMessage.getMessageId(), dupPublishMessage);
        dupPublishMessageCache.put(clientId, messageMap);
    }

    @Override
    public List<DupPublishMessage> get(String clientId) {
        return dupPublishMessageCache.containsKey(clientId) ?
                new ArrayList<>(dupPublishMessageCache.get(clientId).values()) :
                Collections.EMPTY_LIST;
    }

    @Override
    public void remove(String clientId, int messageId) {
        if (!dupPublishMessageCache.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPublishMessage> messageMap = dupPublishMessageCache.get(clientId);
        if (!messageMap.containsKey(messageId)) return;
        messageMap.remove(messageId);
        if (CollectionUtils.isEmpty(messageMap)) {
            dupPublishMessageCache.remove(clientId);
        } else {
            dupPublishMessageCache.put(clientId, messageMap);
        }
    }

    @Override
    public void removeClient(String clientId) {
        if (!dupPublishMessageCache.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPublishMessage> messageMap = dupPublishMessageCache.get(clientId);
        messageMap.forEach((k, v) -> messagePacketIdServer.releaseMessageId(v.getMessageId()));
        messageMap.clear();
        dupPublishMessageCache.remove(clientId);
    }
}
