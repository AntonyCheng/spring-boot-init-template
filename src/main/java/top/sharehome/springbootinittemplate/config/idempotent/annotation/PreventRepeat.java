package top.sharehome.springbootinittemplate.config.idempotent.annotation;

import top.sharehome.springbootinittemplate.config.idempotent.enums.ScopeType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 需要防重的方法注解
 *
 * @author AntonyCheng
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventRepeat {

    /**
     * 接口防重单位时间（默认1000毫秒）
     */
    int time() default 1000;

    /**
     * 时间单位（默认毫秒）
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 作用范围
     */
    ScopeType scopeType() default ScopeType.PERSONAL;

    /**
     * 响应消息
     */
    String message() default "";

}
