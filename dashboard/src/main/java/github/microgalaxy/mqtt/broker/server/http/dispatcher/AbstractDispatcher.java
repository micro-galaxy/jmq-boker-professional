package github.microgalaxy.mqtt.broker.server.http.dispatcher;

import github.microgalaxy.mqtt.broker.server.http.annotaion.Controller;
import github.microgalaxy.mqtt.broker.server.http.annotaion.RequestMethod;
import github.microgalaxy.mqtt.broker.server.http.annotaion.ResourceMapping;
import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.exception.BusinessException;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.exception.HttpException;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.filter.FilterChain;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.interceptor.HandlerInterceptorChain;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.ResponseWrapper;
import github.microgalaxy.mqtt.broker.server.http.resource.FileProcessor;
import github.microgalaxy.mqtt.broker.server.http.util.HttpUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public abstract class AbstractDispatcher implements IDispatcher {
    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final FilterChain filterChain;
    protected final HandlerInterceptorChain handlerInterceptorChain;
    private final Map<RequestMethod, BiConsumer<HttpRequest, HttpResponse>> handlerMap = new HashMap<>();
    protected HttpServerProperties properties;
    protected final Map<String, HandlerInvoke> processHandler = new HashMap<>();

    protected AbstractDispatcher(HttpServerProperties properties) {
        this.properties = properties;
        properties.addOnBeanReadyEvent(cl -> initStaticResourceHandler(properties.getResourcePath()));
        this.filterChain = new FilterChain(properties);
        this.handlerInterceptorChain = new HandlerInterceptorChain(properties);
        handlerMap.put(RequestMethod.GET, this::doGet);
        handlerMap.put(RequestMethod.POST, this::doPost);
        handlerMap.put(RequestMethod.OPTIONS, this::doOptions);
    }


    @Override
    public void doRequest(HttpRequest httpRequest, HttpResponse httpResponse) {
        try {
            if (!ObjectUtils.isEmpty(httpRequest.getBuildEx())) throw httpRequest.getBuildEx();
            filterChain.doPreFilter(httpRequest, httpResponse);
            handlerMap.get(RequestMethod.OPTIONS).accept(httpRequest, httpResponse);
            handlerMap.get(httpRequest.getType()).accept(httpRequest, httpResponse);
            HttpUtils.wireResponse(httpRequest, httpResponse);
        } catch (BusinessException be) {
            httpResponse.addHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON);
            httpResponse.writeJson(ResponseWrapper.error(be.getCode(), Optional.ofNullable(be.getMessage()).orElse("未知异常")));
            HttpUtils.wireResponse(httpRequest, httpResponse);
        } catch (HttpException he) {
            LOGGER.warn(he.getCode() + " " + he.getMessage(), he);
            httpResponse.setStatus(new HttpResponseStatus(he.getCode(), he.getMessage()));
            httpResponse.setEx(he);
            HttpUtils.wireResponse(httpRequest, httpResponse);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            httpResponse.setStatus(new HttpResponseStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR.code(), e.getMessage()));
            httpResponse.setEx(e);
            HttpUtils.wireResponse(httpRequest, httpResponse);
        }
    }

    private void initStaticResourceHandler(String resourcePath) {
        /**
         * classPath: WEB-INF; => resources/WEB-INF
         * /opt/xxx/web; => Server absolute directory
         */
        HandlerInvoke resourceMethod = scanResourceMethod(ResourceMapping.class);
        scanResource(resourcePath)
                .forEach(p -> processHandler.put(String.join("#", RequestMethod.GET.name(), p), resourceMethod));
    }

    private List<String> scanResource(String resourcePath) {
        try {
            return FileProcessor.getClassPathFiles(HttpUtils.resolverPath(resourcePath));
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.exit(1);
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private HandlerInvoke scanResourceMethod(Class<? extends Annotation> clazz) {
        AtomicReference<HandlerInvoke> invokeMethodMap = new AtomicReference<>();
        properties.getApplicationContext().getBeansWithAnnotation(Controller.class).values()
                .forEach(v -> {
                    Method[] methods = v.getClass().getDeclaredMethods();
                    IntStream.range(0, methods.length).forEach(i -> {
                        Annotation resourceAnn = AnnotationUtils.findAnnotation(methods[i], clazz);
                        if (ObjectUtils.isEmpty(resourceAnn)) return;
                        if (!ObjectUtils.isEmpty(invokeMethodMap.get())) {
                            LOGGER.error("There are duplicate static resource URL mappings in ResourceMapping, signature :{}{}.()",
                                    v.getClass().getName(), methods[i].getName());
                            System.exit(1);
                        }
                        Parameter[] parameters = methods[i].getParameters();
                        Class<?>[] parameterTypes = methods[i].getParameterTypes();
                        invokeMethodMap.set(new HandlerInvoke(v, methods[i], new Annotation[]{},
                                true, parameters, parameterTypes, new Annotation[][]{}));
                    });
                });
        return invokeMethodMap.get();
    }

    /**
     * GET request
     *
     * @param httpRequest
     * @param httpResponse
     */
    protected abstract void doGet(HttpRequest httpRequest, HttpResponse httpResponse);

    /**
     * POST request
     *
     * @param httpRequest
     * @param httpResponse
     */
    protected abstract void doPost(HttpRequest httpRequest, HttpResponse httpResponse);

    /**
     * GET request
     *
     * @param httpRequest
     * @param httpResponse
     */
    protected abstract void doOptions(HttpRequest httpRequest, HttpResponse httpResponse);

}
