package io.github.dasrd.iot.link.sdk.service;

import java.lang.annotation.*;

/**
 * 属性
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Property {

    boolean writeable() default true;

    /**
     * @return 属性名，不提供默认为字段名
     */
    String name() default "";

}