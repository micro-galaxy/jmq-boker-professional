package github.microgalaxy.mqtt.broker.internal.model;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public final class InternalIgniteCtrlMessage<T, V> {
    private final T type;
    private final V params;

    public InternalIgniteCtrlMessage(T type, V params) {
        this.type = type;
        this.params = params;
    }

    public T getType() {
        return type;
    }


    public V getParams() {
        return params;
    }

}
