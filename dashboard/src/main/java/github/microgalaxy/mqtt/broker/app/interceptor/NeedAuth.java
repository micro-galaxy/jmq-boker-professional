package github.microgalaxy.mqtt.broker.app.interceptor;

import java.lang.annotation.*;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface NeedAuth {}
