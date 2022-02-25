package github.microgalaxy.mqtt.broker.app.controller;

import github.microgalaxy.mqtt.broker.app.interceptor.NeedAuth;
import github.microgalaxy.mqtt.broker.app.model.NodeForm;
import github.microgalaxy.mqtt.broker.app.service.BrokerMetaServer;
import github.microgalaxy.mqtt.broker.data.model.NodeMetaModel;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestBody;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMapping;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMethod;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RestController;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.util.List;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@RestController
@RequestMapping("/broker/meta")
public class BrokerMetaController {
    @Autowired
    private BrokerMetaServer brokerMetaServer;

    @NeedAuth
    @RequestMapping(path = "/info", type = RequestMethod.GET)
    public ResponseWrapper getMetaInfos() {
        return ResponseWrapper.ok().put(brokerMetaServer.getMetaInfos());
    }

    @NeedAuth
    @RequestMapping(path = "/remove", type = RequestMethod.POST)
    public ResponseWrapper joinClusterIp(@RequestBody NodeForm model) {
        Assert.hasLength(model.getBrokerId(), "BrokerId cant be empty.");
        brokerMetaServer.remove(model.getBrokerId());
        return ResponseWrapper.ok();
    }

}
