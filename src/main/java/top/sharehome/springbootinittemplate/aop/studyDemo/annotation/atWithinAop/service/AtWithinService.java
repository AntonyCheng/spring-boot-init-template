package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.service;

/**
 * 针对切点参数为@within型的切面类的服务接口
 *
 * @author AntonyCheng
 */
public interface AtWithinService {

    void doMethod1();

    String doMethod2();

    void doMethod3() throws Exception;

    void doMethod4(String param);

}