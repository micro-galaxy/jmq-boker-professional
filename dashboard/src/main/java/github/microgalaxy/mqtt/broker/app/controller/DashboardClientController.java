package github.microgalaxy.mqtt.broker.app.controller;

import github.microgalaxy.mqtt.broker.app.interceptor.NeedAuth;
import github.microgalaxy.mqtt.broker.app.model.ClientForm;
import github.microgalaxy.mqtt.broker.app.service.BrokerClientServer;
import github.microgalaxy.mqtt.broker.server.http.annotaion.*;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@RestController
@RequestMapping("/broker/client")
public class DashboardClientController {
    @Autowired
    private BrokerClientServer brokerClientServer;

    @NeedAuth
    @RequestMapping(path = "/page", type = RequestMethod.POST)
    public ResponseWrapper clientPage(@RequestBody ClientForm form) {
        Assert.notNull(form.getCurPage(),"Page index cant be null.");
        Assert.notNull(form.getSize(),"Page size cant be null.");
        return brokerClientServer.clientPage(form);
    }

}
