package top.sharehome.springbootinittemplate.aop.studyDemo.normal.argsAop.service.impl;

import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.argsAop.service.ArgsService;

/**
 * 针对切点参数为args型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class ArgsServiceImpl implements ArgsService {

    public static String demoStatic = "demoStatic";

    @Override
    public void doMethod1() {
        System.out.println("ArgsServiceImpl.doMethod1()");
    }

    @Override
    public String doMethod2() {
        System.out.println("ArgsServiceImpl.doMethod2()");
        return "hello world";
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println("ArgsServiceImpl.doMethod3()");
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String param) {
        System.out.println("ArgsServiceImpl.doMethod4(), args = " + param);
    }

}