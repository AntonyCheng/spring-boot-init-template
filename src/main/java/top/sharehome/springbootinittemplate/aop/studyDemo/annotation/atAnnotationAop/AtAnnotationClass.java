package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop;

import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationAop;

/**
 * 带有自定义注解的类（用于测试@annotation型的切面类）
 *
 * @author AntonyCheng
 */
@Component
public class AtAnnotationClass {

    @AnnotationAop(title = "title", description = "description")
    public String annotationMethod(String classname) {
        return classname+": AtAnnotationClass.annotationMethod()";
    }

}