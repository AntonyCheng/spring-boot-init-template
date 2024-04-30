package top.sharehome.springbootinittemplate.aop.studyDemo.normal.targetAop.service.impl;

import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.targetAop.service.TargetService;

/**
 * 针对切点参数为execution型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class TargetServiceImpl implements TargetService {

    public static String demoStatic = "demoStatic";

    @Override
    public void doMethod1() {
        System.out.println("TargetServiceImpl.doMethod1()");
    }

    @Override
    public String doMethod2() {
        System.out.println("TargetServiceImpl.doMethod2()");
        return "hello world";
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println("TargetServiceImpl.doMethod3()");
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String param) {
        System.out.println("TargetServiceImpl.doMethod4(), args = " + param);
    }

}