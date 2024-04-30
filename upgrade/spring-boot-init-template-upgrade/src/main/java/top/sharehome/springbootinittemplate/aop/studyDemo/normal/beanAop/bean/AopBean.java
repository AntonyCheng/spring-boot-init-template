package top.sharehome.springbootinittemplate.aop.studyDemo.normal.beanAop.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 针对切点参数为bean型的切面类的Bean类
 *
 * @author AntonyCheng
 */
@Component
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AopBean {

    private String arg1;

    private String arg2;

    /**
     * 想要进行AOP的Bean必须将方法名和类名做区分
     *
     * @return 返回Bean
     */
    public AopBean getAopBean() {
        setArg1("arg1");
        setArg2("arg2");
        System.out.println("arg1 = " + arg1);
        System.out.println("arg2 = " + arg2);
        return new AopBean(arg1, arg2);
    }

    public String toString() {
        return "arg1=" + arg1 + ", arg2=" + arg2;
    }

}