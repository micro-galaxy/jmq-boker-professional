package github.microgalaxy.mqtt.broker.data.model;


import lombok.Data;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class TopicSubscribeModel {

    private String clientId;

    private String topic;

    private Integer qos;
}
