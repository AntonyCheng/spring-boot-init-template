package top.sharehome.springbootinittemplate.aop.normal.executionAop.service.impl;

import com.example.aop.service.DemoService;
import org.springframework.stereotype.Service;

@Service
public class DemoServiceImpl implements DemoService {
    @Override
    public void doMethod1() {
        System.out.println("DemoServiceImpl.doMethod1()");
    }

    @Override
    public String doMethod2() {
        System.out.println("DemoServiceImpl.doMethod2()");
        return "hello world";
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println("DemoServiceImpl.doMethod3()");
        throw new Exception("some exception");
    }
}
