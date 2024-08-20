package top.sharehome.springbootinittemplate.config.idempotent.annotation;

import top.sharehome.springbootinittemplate.config.idempotent.enums.ScopeType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 需要限流的方法注解
 *
 * @author AntonyCheng
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimit {

    /**
     * 接口限流单位时间（默认1000毫秒）
     */
    int time() default 1000;

    /**
     * 时间单位（默认毫秒）
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     */
    long rate() default 2;

    /**
     * 每个操作所要消耗的令牌数，该参数值不能大于rate参数值
     */
    long permit() default 1;

    /**
     * 作用范围
     */
    ScopeType scopeType() default ScopeType.ALL;

    /**
     * 响应消息
     */
    String message() default "";

}
