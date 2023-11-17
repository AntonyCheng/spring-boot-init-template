package top.sharehome.springbootinittemplate.aop.normal.executionAop.service.impl;

import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.normal.executionAop.service.ExecutionService;

/**
 * 针对切点参数为execution型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class ExecutionServiceImpl implements ExecutionService {

    @Override
    public void doMethod1() {
        System.out.println("DemoServiceImpl.doMethod1()");
    }

    @Override
    public void doMethod2(String arg) {
        System.out.println("DemoServiceImpl.doMethod2,the arg is " + arg);
    }

    @Override
    public String doMethod3() {
        System.out.println("DemoServiceImpl.doMethod3()");
        return "hello world";
    }

    @Override
    public void doMethod4() throws Exception {
        System.out.println("DemoServiceImpl.doMethod4()");
        throw new Exception("some exception");
    }

}