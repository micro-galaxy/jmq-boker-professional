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
    private IgniteCache<String, ConcurrentHashMap<Integer, DupPublishMessage>> dupPublishMessageCatch;
    @Resource
    private IMessagePacketId messagePacketIdServer;

    @Override
    public void put(String clientId, DupPublishMessage dupPublishMessage) {
        ConcurrentHashMap<Integer, DupPublishMessage> messageMap = dupPublishMessageCatch.containsKey(clientId) ?
                dupPublishMessageCatch.get(clientId) : new ConcurrentHashMap<>();
        messageMap.put(dupPublishMessage.getMessageId(), dupPublishMessage);
        dupPublishMessageCatch.put(clientId, messageMap);
    }

    @Override
    public List<DupPublishMessage> get(String clientId) {
        return dupPublishMessageCatch.containsKey(clientId) ?
                new ArrayList<>(dupPublishMessageCatch.get(clientId).values()) :
                Collections.EMPTY_LIST;
    }

    @Override
    public void remove(String clientId, int messageId) {
        if (!dupPublishMessageCatch.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPublishMessage> messageMap = dupPublishMessageCatch.get(clientId);
        if (!messageMap.containsKey(messageId)) return;
        messageMap.remove(messageId);
        if (CollectionUtils.isEmpty(messageMap)) {
            dupPublishMessageCatch.remove(clientId);
        } else {
            dupPublishMessageCatch.put(clientId, messageMap);
        }
    }

    @Override
    public void removeClient(String clientId) {
        if (!dupPublishMessageCatch.containsKey(clientId)) return;
        ConcurrentHashMap<Integer, DupPublishMessage> messageMap = dupPublishMessageCatch.get(clientId);
        messageMap.forEach((k, v) -> messagePacketIdServer.releaseMessageId(v.getMessageId()));
        messageMap.clear();
        dupPublishMessageCatch.remove(clientId);
    }
}
