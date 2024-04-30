package top.sharehome.springbootinittemplate.aop.studyDemo.normal.targetAop.service;

/**
 * 针对切点参数为execution型的切面类的服务接口
 *
 * @author AntonyCheng
 */
public interface TargetService {

    void doMethod1();

    String doMethod2();

    void doMethod3() throws Exception;

    void doMethod4(String param);

}