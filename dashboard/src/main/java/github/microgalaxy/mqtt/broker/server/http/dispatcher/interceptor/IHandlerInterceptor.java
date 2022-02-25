package github.microgalaxy.mqtt.broker.server.http.dispatcher.interceptor;

import github.microgalaxy.mqtt.broker.server.http.dispatcher.HandlerInvoke;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import org.springframework.core.annotation.Order;

/**
 * support {@link Order},default Integer.MAX_VALUE
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@FunctionalInterface
public interface IHandlerInterceptor {
    /**
     * 前置拦截器
     *
     * @param httpRequest
     * @param httpResponse
     * @param handler
     * @return
     */
    boolean preHandle(HttpRequest httpRequest, HttpResponse httpResponse, HandlerInvoke handler);
}
