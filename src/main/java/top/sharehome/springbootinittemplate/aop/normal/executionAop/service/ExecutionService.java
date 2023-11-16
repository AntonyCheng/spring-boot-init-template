package top.sharehome.springbootinittemplate.aop.normal.executionAop.service;

/**
 * 针对切点参数为execution型的切面类的服务类
 *
 * @author AntonyCheng
 */
public interface ExecutionService {

    public void doMethod1();

    public String doMethod2();

    public void doMethod3() throws Exception;

}