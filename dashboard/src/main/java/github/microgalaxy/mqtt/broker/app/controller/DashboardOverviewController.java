package github.microgalaxy.mqtt.broker.app.controller;

import github.microgalaxy.mqtt.broker.app.interceptor.NeedAuth;
import github.microgalaxy.mqtt.broker.app.service.BrokerOverviewServer;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMapping;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMethod;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RestController;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@RestController
@RequestMapping("/broker/overview")
public class DashboardOverviewController {
    @Autowired
    private BrokerOverviewServer brokerOverviewServer;

    @NeedAuth
    @RequestMapping(path = "/real_time", type = RequestMethod.GET)
    public ResponseWrapper realTimeOverview() {
        return ResponseWrapper.ok().put(brokerOverviewServer.realTimeOverview());
    }
}
