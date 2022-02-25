package github.microgalaxy.mqtt.broker.app.model;

import lombok.Data;

import java.util.Date;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class RealTimeOverviewModel {
    private Integer brokerMetaNum;
    private Integer onlineBrokerNum;
    private Date earliestClusterTime;
    private String brokerVersion;
    private Long inMsgNum;
    private Long outMsgNum;
    private Integer clientNum;
    private Integer subscribeNum;
}
