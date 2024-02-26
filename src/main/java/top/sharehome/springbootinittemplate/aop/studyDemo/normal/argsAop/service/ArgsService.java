package top.sharehome.springbootinittemplate.aop.studyDemo.normal.argsAop.service;

/**
 * 针对切点参数为args型的切面类的服务接口
 *
 * @author AntonyCheng
 */
public interface ArgsService {

    void doMethod1();

    String doMethod2();

    void doMethod3() throws Exception;

    void doMethod4(String param);

}