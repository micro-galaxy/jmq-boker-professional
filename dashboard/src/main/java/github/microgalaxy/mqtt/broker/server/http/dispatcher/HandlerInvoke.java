package github.microgalaxy.mqtt.broker.server.http.dispatcher;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public class HandlerInvoke {

    private final Object targetObject;

    private final Method method;

    private final boolean fileProcess;

    private final Annotation[] methodAnnotations;

    private final Parameter[] parameters;

    private final Class<?>[] paramTypes;

    private final Annotation[][] paramAnnotations;

    HandlerInvoke(Object targetObject, Method method, Annotation[] methodAnnotations, boolean fileProcess,
                  Parameter[] parameters, Class<?>[] paramTypes, Annotation[][] paramAnnotations) {
        this.targetObject = targetObject;
        this.method = method;
        this.fileProcess = fileProcess;
        this.parameters = parameters;
        this.methodAnnotations = methodAnnotations;
        this.paramTypes = paramTypes;
        this.paramAnnotations = paramAnnotations;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public Method getMethod() {
        return method;
    }

    public boolean isFileProcess() {
        return fileProcess;
    }

    public Parameter[] getParameters() {
        return parameters;
    }

    public Annotation[] getMethodAnnotations() {
        return methodAnnotations;
    }

    public Class<?>[] getParamTypes() {
        return paramTypes;
    }

    public Annotation[][] getParamAnnotations() {
        return paramAnnotations;
    }
}
