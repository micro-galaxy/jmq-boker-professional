package github.microgalaxy.mqtt.broker.server.http.annotaion;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Component
public @interface Controller {
}
