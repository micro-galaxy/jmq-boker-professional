package github.microgalaxy.mqtt.broker.event.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TopicSubModel extends TopicUnSubModel implements Serializable {
    private Integer qos;
}
