package github.microgalaxy.mqtt.broker.data.model;


import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class NodeMetaModel {

    private String brokerId;

    private String ip;

    private Integer mqttPort;

    private Integer mqttWsPort;

    private String wsPath;

    private Integer httpPort;

    private Date joinTime;
}
