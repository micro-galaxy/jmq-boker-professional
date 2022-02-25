package github.microgalaxy.mqtt.broker.app.controller;

import github.microgalaxy.mqtt.broker.app.interceptor.NeedAuth;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestBody;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMapping;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMethod;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RestController;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import github.microgalaxy.mqtt.broker.user.IDashboardUser;
import github.microgalaxy.mqtt.broker.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@RestController
@RequestMapping("/broker/user")
public class DashboardUserController {
    @Autowired
    private IDashboardUser dashboardUserServer;

    @RequestMapping(path = "/login", type = RequestMethod.POST)
    public ResponseWrapper login(@RequestBody User model) {
        Assert.hasLength(model.getUsername(), "Username cant be empty.");
        Assert.hasLength(model.getPassword(), "Password cant be empty.");
        return dashboardUserServer.userAuth(model);
    }

    @NeedAuth
    @RequestMapping(path = "/re_password", type = RequestMethod.POST)
    public ResponseWrapper rePassword(@RequestBody User model) {
        Assert.hasLength(model.getUsername(), "Username cant be empty.");
        Assert.hasLength(model.getPassword(), "Old password cant be empty.");
        Assert.hasLength(model.getNewPassword(), "New password cant be empty.");
        return dashboardUserServer.userRePassword(model);
    }
}
