package github.microgalaxy.mqtt.broker.server.http.dispatcher.filter;

import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class FilterChain {
    private final HttpServerProperties properties;

    private List<IFilter> preFilters = Collections.EMPTY_LIST;

    public FilterChain(HttpServerProperties properties) {
        this.properties = properties;
        properties.addOnBeanReadyEvent(this::initFilterMapping);
    }

    public void doPreFilter(HttpRequest httpRequest, HttpResponse httpResponse) {
        IntStream.range(0, preFilters.size())
                .forEach(i -> preFilters.get(i).doFilter(httpRequest, httpResponse));
    }


    private void initFilterMapping(Class<?> eventClass) {
        preFilters = this.properties.getApplicationContext().getBeansOfType(IFilter.class)
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
