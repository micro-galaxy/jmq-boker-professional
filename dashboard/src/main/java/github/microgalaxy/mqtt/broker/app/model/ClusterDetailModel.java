package github.microgalaxy.mqtt.broker.app.model;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class ClusterDetailModel {
    private Integer brokerMetaNum;
    private String brokerVersion;
    private Integer clientNum;
    private Integer subscribeNum;
    private List<CusterNode> nodes;

    @Data
    public static class CusterNode{
        private String brokerId;
        private String ip;
        private Boolean active;
        private Date occurTime;
    }
}
