package top.sharehome.springbootinittemplate.config.log.annotation;

import top.sharehome.springbootinittemplate.config.log.enums.Operator;

import java.lang.annotation.*;

/**
 * 日志注解
 *
 * @author AntonyCheng
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface ControllerLog {

    /**
     * 操作描述
     */
    String description() default "";

    /**
     * 操作类型（0其他1增2删3查4改5导入6导出）
     */
    Operator operator() default Operator.OTHER;

    /**
     * 屏蔽请求/响应参数
     */
    String[] maskParams() default {};

}
