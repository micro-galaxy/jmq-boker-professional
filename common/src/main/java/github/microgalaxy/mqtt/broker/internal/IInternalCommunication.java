package github.microgalaxy.mqtt.broker.internal;

/**
 * Internal communication interface for client clusters
 *
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public interface IInternalCommunication<T> {
    /**
     * InternalMessage arrives
     *
     * @param message
     */
    void onInternalMessage(T message);
}
