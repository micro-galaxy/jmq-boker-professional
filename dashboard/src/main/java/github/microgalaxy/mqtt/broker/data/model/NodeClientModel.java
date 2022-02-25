package github.microgalaxy.mqtt.broker.data.model;


import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class NodeClientModel {

    private String clientId;

    private Integer userName;

    private String ipPort;
    /**
     * 会话过期时间：s, mqtt5功能
     */
    private Integer sessionExpiryInterval;

    private Long subscribeNumber;

    private boolean connected;

    private Date createTime;
}
