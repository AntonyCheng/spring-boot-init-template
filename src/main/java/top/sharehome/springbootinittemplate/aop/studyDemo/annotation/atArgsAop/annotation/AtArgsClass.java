package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.annotation;

import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.annotation.param.Demo;

/**
 * 带有自定义注解的类（用于测试@args型的切面类）
 *
 * @author AntonyCheng
 */
@Component
public class AtArgsClass {

    public String annotationMethod(Demo param) {
        return param.getValue() + ": AtArgsClass.annotationMethod()";
    }

}