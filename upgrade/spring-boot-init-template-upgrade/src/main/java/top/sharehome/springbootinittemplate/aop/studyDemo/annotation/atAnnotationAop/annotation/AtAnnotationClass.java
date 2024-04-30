package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop.annotation;

import org.springframework.stereotype.Component;

/**
 * 带有自定义注解的类（用于测试@annotation型的切面类）
 *
 * @author AntonyCheng
 */
@Component
public class AtAnnotationClass {

    @AtAnnotationAop(title = "title", description = "description")
    public String annotationMethod(String param) {
        return param + ": AtAnnotationClass.annotationMethod()";
    }

}