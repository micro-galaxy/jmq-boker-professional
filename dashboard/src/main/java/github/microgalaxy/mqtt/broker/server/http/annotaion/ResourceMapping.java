package github.microgalaxy.mqtt.broker.server.http.annotaion;

import java.lang.annotation.*;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface ResourceMapping {
}
