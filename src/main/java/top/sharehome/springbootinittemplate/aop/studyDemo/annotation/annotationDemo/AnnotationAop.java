package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo;

import java.lang.annotation.*;

/**
 * 示例自定义注解，用来做AOP测试。
 * Target注解必须含有ElementType.METHOD，因为Spring AOP
 *
 * @author AntonyCheng
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnnotationAop {

    public String title() default "";

    public String description() default "";

    public boolean isTrue() default false;

}
