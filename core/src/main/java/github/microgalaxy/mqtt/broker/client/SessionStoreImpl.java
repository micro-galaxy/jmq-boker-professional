package github.microgalaxy.mqtt.broker.client;

import github.microgalaxy.mqtt.broker.config.BrokerConstant;
import javafx.print.Collation;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.cache.query.QueryCursor;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.SqlQuery;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import javax.cache.Cache;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Component
public class SessionStoreImpl implements ISessionStore {
    private final Map<String, Session> nodeSessionCache = new ConcurrentHashMap<>();
    @Resource
    private IgniteCache<String, Session> clientSessionCache;

    @Override
    public void put(String clientId, Session session) {
        clientSessionCache.put(clientId, session);
        nodeSessionCache.put(clientId, session);
    }

    @Override
    public Session get(String clientId) {
        Session globalSession = clientSessionCache.get(clientId);
        if (ObjectUtils.isEmpty(globalSession)) return null;
        Session nodeSession = nodeSessionCache.get(clientId);
        if (!ObjectUtils.isEmpty(nodeSession)) globalSession = nodeSession;
        return globalSession;
    }

    @Override
    public void remove(String clientId) {
        clientSessionCache.remove(clientId);
        nodeSessionCache.remove(clientId);
    }

    public Map<String, Object> clientPage(Map<String, Object> queryParams, Integer page, Integer pageSize) {
        int total = clientSessionCache.size();
        String condition = sqlCondition(queryParams);
        String sqlStr = String.join("",
                BrokerConstant.SqlUtil.SELECT, Session.class.getSimpleName(),
                BrokerConstant.SqlUtil.WHERE, condition,
                BrokerConstant.SqlUtil.LIMIT, String.valueOf(page * pageSize), BrokerConstant.SqlUtil.OFFSET, String.valueOf((page - 1) * pageSize));
        SqlQuery<String, Session> sql = new SqlQuery<>(Session.class, sqlStr);
        try (QueryCursor<Cache.Entry<String, Session>> cursor = clientSessionCache.query(sql)) {
            List<Session> sessionList = cursor.getAll().stream().map(Cache.Entry::getValue).collect(Collectors.toList());
            return new HashMap<String, Object>(2) {{
                put("data", sessionList);
                put("total", total);
            }};
        }
    }

    private String sqlCondition(Map<String, Object> queryParams) {
        if (CollectionUtils.isEmpty(queryParams)) return BrokerConstant.SqlUtil.TRUE;
        return queryParams.entrySet().stream().map(en -> String.join(BrokerConstant.SqlUtil.EQUALS, en.getKey(),
                String.join(en.getValue().toString(), BrokerConstant.SqlUtil.STRING_CHARACTER, BrokerConstant.SqlUtil.STRING_CHARACTER)))
                .collect(Collectors.joining(BrokerConstant.SqlUtil.AND));
    }
}
