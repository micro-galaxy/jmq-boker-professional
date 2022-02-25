package github.microgalaxy.mqtt.broker.app.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Data
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class PageBaseModel {
    private Integer total;
    private Integer curPage;
    private Integer size;
}
