package github.microgalaxy.mqtt.broker.app.model;

import lombok.Data;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
public class ClientForm extends PageBaseModel{
    private String brokerId;

    private String clientId;

    private String username;
}
