package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atTargetAop.annotation;

import org.springframework.stereotype.Component;

/**
 * 带有自定义注解的类（用于测试@target型的切面类）
 *
 * @author AntonyCheng
 */
@Component
@AtTargetAop(title = "title", description = "description")
public class AtTargetClass {

    public String annotationMethod(String param) {
        return param + ": AtTargetClass.annotationMethod()";
    }

}