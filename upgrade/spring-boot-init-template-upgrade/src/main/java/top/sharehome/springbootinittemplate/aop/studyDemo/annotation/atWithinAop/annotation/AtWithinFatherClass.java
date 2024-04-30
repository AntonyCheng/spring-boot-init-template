package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.annotation;

import org.springframework.stereotype.Component;

/**
 * 带有自定义注解的父类（用于测试@within型的切面类）
 *
 * @author AntonyCheng
 */
@Component
@AtWithinAop(title = "title", description = "description")
public class AtWithinFatherClass {

    public String annotationMethod(String param) {
        return param + ": AtWithinFatherClass.annotationMethod()";
    }

}