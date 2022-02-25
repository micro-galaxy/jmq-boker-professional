package github.microgalaxy.mqtt.broker.event.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class ClientDisConnectedModel {
    private String clientId;
    private String username;
    private String ip;
    private Date occurTime;
}
