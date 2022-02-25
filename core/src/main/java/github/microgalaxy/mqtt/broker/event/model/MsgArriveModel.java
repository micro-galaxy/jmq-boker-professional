package github.microgalaxy.mqtt.broker.event.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class MsgArriveModel {
    private String clientId;
    private String username;
    private String topic;
    private Integer qos;
    private Boolean retain;
    private byte[] payload;
    private Date occurTime;
}
