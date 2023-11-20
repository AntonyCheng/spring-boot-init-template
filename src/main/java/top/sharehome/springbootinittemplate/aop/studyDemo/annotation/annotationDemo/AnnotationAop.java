package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo;

import java.lang.annotation.*;

/**
 * 示例自定义注解，用来做AOP测试。
 * Target注解建议至少含有ElementType.METHOD，因为Spring中的AOP只是方法执行；
 * Retention注解建议选择RetentionPolicy.RUNTIME，让该注解保留至运行时。
 *
 * @author AntonyCheng
 */
@Target({ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnnotationAop {

    public String title() default "";

    public String description() default "";

    public boolean isTrue() default false;

}
