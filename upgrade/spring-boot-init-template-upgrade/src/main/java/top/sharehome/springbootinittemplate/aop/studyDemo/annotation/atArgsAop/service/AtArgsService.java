package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.service;

/**
 * 针对切点参数为@args型的切面类的服务接口
 *
 * @author AntonyCheng
 */
public interface AtArgsService {

    void doMethod1();

    String doMethod2();

    void doMethod3() throws Exception;

    void doMethod4(String param);

}