package github.microgalaxy.mqtt.broker.server.http.annotaion;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author Microgalaxy（https://github.com/micro-galaxy）
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface RequestParam {
    @AliasFor("value")
    String path() default "";

    @AliasFor("path")
    String value() default "";
}
