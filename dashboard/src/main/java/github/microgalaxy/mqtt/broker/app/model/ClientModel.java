package github.microgalaxy.mqtt.broker.app.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class ClientModel {
    private String deviceId;
    private String username;
    private String ip;
    private Integer subscribeNum;
    private Integer keepalive;
    private String protocol;
    private Boolean status;
    private Date time;
}
