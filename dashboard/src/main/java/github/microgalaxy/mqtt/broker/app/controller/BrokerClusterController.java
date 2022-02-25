package github.microgalaxy.mqtt.broker.app.controller;

import github.microgalaxy.mqtt.broker.app.interceptor.NeedAuth;
import github.microgalaxy.mqtt.broker.app.model.NodeForm;
import github.microgalaxy.mqtt.broker.app.service.BrokerClusterServer;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestBody;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMapping;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMethod;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RestController;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@RestController
@RequestMapping("/broker/cluster")
public class BrokerClusterController {
    @Autowired
    private BrokerClusterServer brokerClusterServer;

    @NeedAuth
    @RequestMapping(path = "/info", type = RequestMethod.GET)
    public ResponseWrapper getClusterInfo() {
       return ResponseWrapper.ok().put(brokerClusterServer.getClusterInfo());
    }

    @NeedAuth
    @RequestMapping(path = "/detail", type = RequestMethod.GET)
    public ResponseWrapper getClusterDetail() {
        return ResponseWrapper.ok().put(brokerClusterServer.getClusterDetail());
    }

    @NeedAuth
    @RequestMapping(path = "/ip/join", type = RequestMethod.POST)
    public ResponseWrapper joinClusterIp(@RequestBody NodeForm model) {
        Assert.hasLength(model.getBrokerId(),"BrokerId cant be empty.");
        Assert.hasLength(model.getIp(),"Ip cant be empty.");
        brokerClusterServer.joinClusterIp(model);
        return ResponseWrapper.ok();
    }

    @NeedAuth
    @RequestMapping(path = "/multicast/join", type = RequestMethod.POST)
    public ResponseWrapper joinClusterMulticastIp(@RequestBody NodeForm model) {
        Assert.hasLength(model.getBrokerId(),"BrokerId cant be empty.");
        Assert.hasLength(model.getIp(),"Ip cant be empty.");
        brokerClusterServer.joinClusterMulticastIp(model);
        return ResponseWrapper.ok();
    }

    @NeedAuth
    @RequestMapping(path = "/left_cluster", type = RequestMethod.POST)
    public ResponseWrapper leftCluster(@RequestBody NodeForm model) {
        Assert.hasLength(model.getBrokerId(),"BrokerId cant be empty.");
        brokerClusterServer.leftCluster(model);
        return ResponseWrapper.ok();
    }

    @NeedAuth
    @RequestMapping(path = "/reconnect_cluster", type = RequestMethod.POST)
    public ResponseWrapper reconnectCluster(@RequestBody NodeForm model) {
        Assert.hasLength(model.getBrokerId(),"BrokerId cant be empty.");
        brokerClusterServer.reconnectCluster(model);
        return ResponseWrapper.ok();
    }

}
