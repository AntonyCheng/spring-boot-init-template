package top.sharehome.springbootinittemplate.config.idempotent.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * 需要请求幂等的方法注解
 *
 * @author AntonyCheng
 */
@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Idempotent {

    /**
     * 接口幂等请求时间（默认1000毫秒）
     */
    int time() default 1000;

    /**
     * 时间单位（默认毫秒）
     */
    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

    /**
     * 响应消息
     */
    String message() default "";

}
