package github.microgalaxy.mqtt.broker.app.service;

import github.microgalaxy.mqtt.broker.app.model.ClientForm;
import github.microgalaxy.mqtt.broker.app.model.ClientModel;
import github.microgalaxy.mqtt.broker.client.Session;
import github.microgalaxy.mqtt.broker.client.SessionStoreImpl;
import github.microgalaxy.mqtt.broker.data.MonitorDataManager;
import github.microgalaxy.mqtt.broker.event.model.TopicSubModel;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Service
public class BrokerClientServer {
    @Autowired
    private SessionStoreImpl sessionServer;
    @Autowired
    private MonitorDataManager monitorDataManager;



    public ResponseWrapper clientPage(ClientForm form) {
        Map<String, Object> queryParams = new HashMap<String, Object>() {{
            if (!ObjectUtils.isEmpty(form.getBrokerId())) put("jmqId", form.getBrokerId());
            if (!ObjectUtils.isEmpty(form.getClientId())) put("clientId", form.getClientId());
            if (!ObjectUtils.isEmpty(form.getUsername())) put("username", form.getUsername());
        }};
        Map<String, Object> resMap = sessionServer.clientPage(queryParams, form.getCurPage(), form.getSize());
        List<Session> sessionList = (List<Session>) resMap.get("data");
       Map<String, List<TopicSubModel>> clientSubMap = monitorDataManager
               .getClientSubscribeInfos(sessionList.stream().map(Session::getClientId).collect(Collectors.toList()));
        List<ClientModel> models = sessionList.stream().map(v -> {
            ClientModel clientModel = new ClientModel();
            clientModel.setDeviceId(v.getClientId());
            clientModel.setUsername(v.getUsername());
            clientModel.setIp(v.getIp());
            clientModel.setSubscribeNum(clientSubMap.get(v.getClientId()).size());
            clientModel.setKeepalive(v.getKeepAlive());
            clientModel.setProtocol(v.getMqttProtocolVersion().name());
            clientModel.setStatus(true);
            clientModel.setTime(new Date(v.getCreateTimestamp()));
            return clientModel;

        }).collect(Collectors.toList());

        return ResponseWrapper.ok()
                .put(models)
                .put("total", resMap.get("total"));
    }
}
