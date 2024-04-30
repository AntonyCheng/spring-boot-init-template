package top.sharehome.springbootinittemplate.aop.studyDemo.normal.withinAop.service;

/**
 * 针对切点参数为within型的切面类的服务接口
 *
 * @author AntonyCheng
 */
public interface WithinService {

    void doMethod1();

    String doMethod2();

    void doMethod3() throws Exception;

    void doMethod4(String param);

}