package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo;

import org.springframework.stereotype.Component;

/**
 * 带有自定义注解的类（用于测试AOP）
 *
 * @author AntonyCheng
 */
@Component
public class AnnotationClass {

    @AnnotationAop(title = "title", description = "description")
    public String annotationMethod(String classname) {
        return classname+": AnnotationClass.annotationMethod()";
    }

}