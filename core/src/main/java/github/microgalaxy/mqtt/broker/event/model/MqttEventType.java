package github.microgalaxy.mqtt.broker.event.model;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
public enum MqttEventType {
    CLIENT_CONNECT, CLIENT_DISCONNECT, MSG_ARRIVE, MSG_PUSH, MSG_DISCARD,SUB_TOPIC,UNSUB_TOPIC
}
