package github.microgalaxy.mqtt.broker.server.http.resource;

import github.microgalaxy.mqtt.broker.server.http.annotaion.Controller;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMapping;
import github.microgalaxy.mqtt.broker.server.http.annotaion.ResourceMapping;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.Request;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.Response;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Controller
public class DefaultResourceController {

    @RequestMapping("/")
    public String index() {
        return "/index.html";
    }

    @ResourceMapping
    public String resourceMapping(Request request, Response response) {
        return request.getPath();
    }

}
