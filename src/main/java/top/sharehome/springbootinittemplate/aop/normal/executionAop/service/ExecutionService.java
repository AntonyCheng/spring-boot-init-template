package top.sharehome.springbootinittemplate.aop.normal.executionAop.service;

/**
 * 针对切点参数为execution型的切面类的服务类
 *
 * @author AntonyCheng
 */
public interface ExecutionService {

    public void doMethod1();

    public void doMethod2(String arg);

    public String doMethod3();

    public void doMethod4() throws Exception;

}