package github.microgalaxy.mqtt.broker.event.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class TopicUnSubModel {
    private String clientId;
    private String username;
    private String topic;
    private Date occurTime;
}
