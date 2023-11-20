package top.sharehome.springbootinittemplate.aop.studyDemo.normal.thisAop.service.impl;

import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.thisAop.service.ThisService;

/**
 * 针对切点参数为execution型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class ThisServiceImpl implements ThisService {

    public static String demoStatic = "demoStatic";

    @Override
    public void doMethod1() {
        System.out.println("ThisServiceImpl.doMethod1()");
    }

    @Override
    public String doMethod2() {
        System.out.println("ThisServiceImpl.doMethod2()");
        return "hello world";
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println("ThisServiceImpl.doMethod3()");
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String demo) {
        System.out.println("ThisServiceImpl.doMethod4(), args = " + demo);
    }

}