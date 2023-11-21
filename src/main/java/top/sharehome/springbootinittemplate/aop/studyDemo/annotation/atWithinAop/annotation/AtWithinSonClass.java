package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.annotation;

import org.springframework.stereotype.Component;

/**
 * 带有自定义注解的子类（用于测试@within型的切面类）
 *
 * @author AntonyCheng
 */
@Component
public class AtWithinSonClass extends AtWithinFatherClass {

    @Override
    public String annotationMethod(String param) {
        return param + ": AtWithinSonClass.annotationMethod()";
    }

}