package github.microgalaxy.mqtt.broker.server.http.dispatcher.interceptor;

import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.HandlerInvoke;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class HandlerInterceptorChain {
    private final HttpServerProperties properties;

    private List<IHandlerInterceptor> preHandlers;

    public HandlerInterceptorChain(HttpServerProperties properties) {
        this.properties = properties;
        properties.addOnBeanReadyEvent(this::initHandlerMapping);
    }

    public boolean doPreHandle(HttpRequest httpRequest, HttpResponse httpResponse, HandlerInvoke handler) {
        return IntStream.range(0, preHandlers.size())
                .allMatch(i -> preHandlers.get(i).preHandle(httpRequest, httpResponse, handler));
    }


    private void initHandlerMapping(Class<?> eventClass) {
        preHandlers = this.properties.getApplicationContext().getBeansOfType(IHandlerInterceptor.class)
                .values()
                .stream()
                .sorted((a, b) -> {
                    Order aSort = AnnotationUtils.findAnnotation(a.getClass(), Order.class);
                    Order bSort = AnnotationUtils.findAnnotation(b.getClass(), Order.class);
                    return (ObjectUtils.isEmpty(aSort) ? Integer.MIN_VALUE : aSort.value()) -
                            (ObjectUtils.isEmpty(bSort) ? Integer.MIN_VALUE : bSort.value());
                })
                .collect(Collectors.toList());
    }
}
