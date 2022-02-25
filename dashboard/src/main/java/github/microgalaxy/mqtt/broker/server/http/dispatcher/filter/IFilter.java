package github.microgalaxy.mqtt.broker.server.http.dispatcher.filter;

import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import org.springframework.core.annotation.Order;

/**
 * support {@link Order},default Integer.MAX_VALUE
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public interface IFilter {
    /**
     * doFilter
     *
     * @param httpRequest
     * @param httpResponse
     */
    void doFilter(HttpRequest httpRequest, HttpResponse httpResponse);
}
