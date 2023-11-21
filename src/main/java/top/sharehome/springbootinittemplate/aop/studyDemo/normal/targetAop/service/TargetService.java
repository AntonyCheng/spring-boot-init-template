package top.sharehome.springbootinittemplate.aop.studyDemo.normal.targetAop.service;

/**
 * 针对切点参数为execution型的切面类的服务接口
 *
 * @author AntonyCheng
 */
public interface TargetService {

    public void doMethod1();

    public String doMethod2();

    public void doMethod3() throws Exception;

    public void doMethod4(String param);

}