package github.microgalaxy.mqtt.broker.event.model;

import io.netty.handler.codec.mqtt.MqttVersion;
import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class ClientConnectedModel {
    private String clientId;
    private String username;
    private Integer keepAlive;
    private String ip;
    private MqttVersion protocolVersion;
    private Date occurTime;
}
