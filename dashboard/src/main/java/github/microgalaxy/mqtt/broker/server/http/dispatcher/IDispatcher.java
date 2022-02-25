package github.microgalaxy.mqtt.broker.server.http.dispatcher;

import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public interface IDispatcher {
    /**
     * 处理http请求
     *
     * @param httpRequest
     * @param httpResponse
     */
    void doRequest(HttpRequest httpRequest, HttpResponse httpResponse);
}
