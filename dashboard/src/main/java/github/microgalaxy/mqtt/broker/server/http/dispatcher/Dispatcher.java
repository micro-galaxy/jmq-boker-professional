package github.microgalaxy.mqtt.broker.server.http.dispatcher;

import github.microgalaxy.mqtt.broker.server.http.annotaion.*;
import github.microgalaxy.mqtt.broker.server.http.config.HttpServerProperties;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.exception.BusinessException;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.exception.HttpException;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpRequest;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.HttpResponse;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.Request;
import github.microgalaxy.mqtt.broker.server.http.dispatcher.model.Response;
import github.microgalaxy.mqtt.broker.server.http.resource.FileProcessor;
import github.microgalaxy.mqtt.broker.server.http.util.HttpUtils;
import github.microgalaxy.mqtt.broker.util.JsonUtils;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.AsciiString;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.IntStream;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class Dispatcher extends AbstractDispatcher {

    public Dispatcher(HttpServerProperties properties) {
        super(properties);
        properties.addOnBeanReadyEvent(this::initRequestMapping);
    }

    @Override
    protected void doGet(HttpRequest httpRequest, HttpResponse httpResponse) {
        doPost(httpRequest, httpResponse);
    }

    @Override
    protected void doPost(HttpRequest httpRequest, HttpResponse httpResponse) {
        String path = httpRequest.getPath().replaceAll("//+", "/").toLowerCase();
        HandlerInvoke handler = processHandler.get(String.join("#", httpRequest.getType().name(), path));
        LOGGER.info("Processing {} http request for[{}]", httpRequest.getType().name(), path);
        try {
            if (ObjectUtils.isEmpty(handler))
                throw new HttpException(HttpResponseStatus.NOT_FOUND.code(), HttpResponseStatus.NOT_FOUND.reasonPhrase() + ":" + path + " " + httpRequest.getType());
            //handler interceptor
            boolean ok = handlerInterceptorChain.doPreHandle(httpRequest, httpResponse, handler);
            if (!ok) return;
            //invoke mapping handler
            Object returnParam = handler.getMethod().invoke(handler.getTargetObject(), parseParams(httpRequest, httpResponse, handler.getParameters(), handler.getParamTypes(),
                    handler.getParamAnnotations()));
            //file parsing
            if (handler.isFileProcess()) {
                Byte[] fileBytes = FileProcessor.parseJarFile(properties.getResourcePath(), returnParam.toString());
                AsciiString contentType = HttpUtils.fileContentType(returnParam.toString());
                httpResponse.write(fileBytes, contentType);
                LOGGER.info("Processed {} http file request for[{}]", httpRequest.getType().name(), path);
                return;
            }
            //json parsing
            httpResponse.writeJson(returnParam);
            LOGGER.info("Processed {} http request for[{}] Written[{}]", httpRequest.getType().name(), path,returnParam);
        } catch (HttpException he) {
            throw he;
        } catch (InvocationTargetException ie) {
            LOGGER.error(ie.getTargetException().getMessage(), ie.getTargetException());
            if (ie.getTargetException() instanceof BusinessException)
                throw (BusinessException) ie.getTargetException();
            throw new BusinessException(ie.getTargetException().getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //Set aside global exception handling
            throw new BusinessException(e.getMessage());
        }
    }


    private Object[] parseParams(HttpRequest httpRequest, HttpResponse httpResponse, Parameter[] parameters, Class<?>[] paramTypes, Annotation[][] paramAnnotations) {
        return IntStream.range(0, paramTypes.length).mapToObj(i -> {
            Class<?> type = paramTypes[i];
            if (type.equals(Request.class)) return (Request) httpRequest;
            if (type.equals(Response.class)) return (Response) httpResponse;
            List<Annotation> annotations = Arrays.asList(paramAnnotations[i]);
            //RequestHeader
            Optional<Annotation> optionalHeader = annotations.stream().filter(a -> RequestHeader.class.equals(a.annotationType())).findFirst();
            if (optionalHeader.isPresent()) {
                RequestHeader requestHeader = (RequestHeader) optionalHeader.get();
                return httpRequest.getHeader().get(
                        Optional.of(requestHeader.value())
                                .filter(h -> !StringUtils.isEmpty(h)).orElse(parameters[i].getName())
                );
            }
            //TODO Support @PathVariable in the future
            //RequestParam
            Optional<Annotation> optionalParam = annotations.stream().filter(a -> RequestParam.class.equals(a.annotationType())).findFirst();
            if (optionalParam.isPresent()) {
                RequestParam requestParam = (RequestParam) optionalParam.get();
                return httpRequest.getUriParams().get(
                        Optional.of(requestParam.value())
                                .filter(h -> !StringUtils.isEmpty(h)).orElse(parameters[i].getName())
                );
            }
            //RequestBody
            Optional<Annotation> optionalBody = annotations.stream().filter(a -> RequestBody.class.equals(a.annotationType())).findFirst();
            if (optionalBody.isPresent()) {
                return JsonUtils.beanToModel(httpRequest.getBodyParams(), type);
            }
            return null;
        }).toArray();
    }


    @Override
    protected void doOptions(HttpRequest httpRequest, HttpResponse httpResponse) {
        HttpHeaders header = httpResponse.getHeader();
        header.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_CREDENTIALS, true);
        header.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, String.join(",",
                HttpHeaderNames.CONTENT_TYPE.toString(), HttpHeaderNames.AUTHORIZATION.toString(), HttpHeaderNames.COOKIE.toString()));
        header.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "GET,POST,PUT,DELETE,OPTIONS");
        header.add(HttpHeaderNames.ACCESS_CONTROL_MAX_AGE, 3600);
        String referer = httpRequest.getHeader().get(HttpHeaderNames.REFERER);
        if (StringUtils.isEmpty(referer)) {
            header.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        } else {
            String refererPath = referer.replace(httpRequest.getHttpVersion().protocolName().toLowerCase() + "://", "");
            refererPath = refererPath.substring(0, refererPath.indexOf("/"));
            header.add(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, httpRequest.getHttpVersion().protocolName().toLowerCase() + "://" + refererPath);
        }
    }

    private void initRequestMapping(Class<?> eventClass) {
        Collection<Object> processHandlerObjects = new ArrayList<>();
        processHandlerObjects.addAll(properties.getApplicationContext().getBeansWithAnnotation(RestController.class).values());
        processHandlerObjects.addAll(properties.getApplicationContext().getBeansWithAnnotation(Controller.class).values());
        processHandlerObjects.forEach(v -> {
            RequestMapping rootMapping = AnnotationUtils.findAnnotation(v.getClass(), RequestMapping.class);
            Method[] handlerMethod = v.getClass().getDeclaredMethods();
            IntStream.range(0, handlerMethod.length)
                    .forEach(i -> {
                        RequestMapping mRequestMapping = AnnotationUtils.findAnnotation(handlerMethod[i], RequestMapping.class);
                        if (ObjectUtils.isEmpty(mRequestMapping)) return;
                        String uri = String.join("", !ObjectUtils.isEmpty(rootMapping) ? rootMapping.value() : "", mRequestMapping.value());
                        String uriKey = String.join("#", mRequestMapping.type().name(), uri);
                        if (!ObjectUtils.isEmpty(processHandler.get(uriKey))) {
                            LOGGER.error("Duplicate uri in RequestMapping, signature :{}{}()", v.getClass().getName(), handlerMethod[i].getName());
                            System.exit(1);
                        }
                        processHandler.put(uriKey, getInvoke(v, handlerMethod[i]));
                    });
        });
    }

    private HandlerInvoke getInvoke(Object targetObject, Method method) {
        Parameter[] parameters = method.getParameters();
        Class<?>[] parameterTypes = method.getParameterTypes();
        Annotation[] fieldAnnotations = method.getAnnotations();
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Controller controller = AnnotationUtils.findAnnotation(targetObject.getClass(), Controller.class);
        RestController restController = AnnotationUtils.findAnnotation(targetObject.getClass(), RestController.class);
        boolean fileProcess = ObjectUtils.isEmpty(restController) && !ObjectUtils.isEmpty(controller)
                && Arrays.stream(fieldAnnotations).noneMatch(an -> ResponseBody.class.equals(an.annotationType()));
        return new HandlerInvoke(targetObject, method, fieldAnnotations, fileProcess, parameters, parameterTypes, parameterAnnotations);
    }
}
