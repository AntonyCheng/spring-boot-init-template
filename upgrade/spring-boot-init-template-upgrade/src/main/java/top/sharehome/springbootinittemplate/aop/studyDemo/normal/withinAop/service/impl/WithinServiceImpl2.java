package top.sharehome.springbootinittemplate.aop.studyDemo.normal.withinAop.service.impl;

import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.withinAop.service.WithinService;

/**
 * 针对切点参数为within型的切面类的服务实现类2
 *
 * @author AntonyCheng
 */
@Service
public class WithinServiceImpl2 implements WithinService {

    @Override
    public void doMethod1() {
        System.out.println("WithinServiceImpl2.doMethod2-1()");
    }

    @Override
    public String doMethod2() {
        System.out.println("WithinServiceImpl2.doMethod2-2()");
        return "hello world";
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println("WithinServiceImpl2.doMethod2-3()");
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String param) {
        System.out.println("WithinServiceImpl2.doMethod2-4(), args = " + param);
    }

}