package github.microgalaxy.mqtt.broker.data.model;


import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class NodeClusterModel {

    private String brokerId;

    private String ip;

    private Integer port;

    private boolean active;

    private Date occurTime;

    private String reason;
}
