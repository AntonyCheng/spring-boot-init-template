package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop;

import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationAop;

/**
 * 带有自定义注解的类（用于测试@annotation型的切面类）
 *
 * @author AntonyCheng
 */
@Component
public class AtArgsClass {

    public String annotationMethod(@AnnotationAop String classname) {
        return classname + ": AtArgsClass.annotationMethod()";
    }

}