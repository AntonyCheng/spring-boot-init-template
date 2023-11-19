package top.sharehome.springbootinittemplate.aop.normal.beanAop.bean;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * 针对切点参数为bean型的切面类的Bean类
 *
 * @author AntonyCheng
 */
@Component
public class DemoBean {

    @Bean
    public DemoBean demoBean() {
        return new DemoBean();
    }

}