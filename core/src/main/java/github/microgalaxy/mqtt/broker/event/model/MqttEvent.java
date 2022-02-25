package github.microgalaxy.mqtt.broker.event.model;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public final class MqttEvent<K> {
    private final K type;
    private final Object payload;

    public MqttEvent(K type, Object payload) {
        this.type = type;
        this.payload = payload;
    }

    public K getType() {
        return type;
    }


    public Object getPayload() {
        return payload;
    }

}
