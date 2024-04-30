package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.annotation.AtArgsClass;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.annotation.param.Demo;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.service.AtArgsService;

/**
 * 针对切点参数为@args型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class AtArgsServiceImpl implements AtArgsService {

    @Resource
    private AtArgsClass atArgsClass;

    @Override
    public void doMethod1() {
        System.out.println(atArgsClass.annotationMethod(new Demo("AtArgsServiceImpl.doMethod1()")));
    }

    @Override
    public String doMethod2() {
        return atArgsClass.annotationMethod(new Demo("AtArgsServiceImpl.doMethod2()"));
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println(atArgsClass.annotationMethod(new Demo("AtArgsServiceImpl.doMethod3()")));
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String param) {
        System.out.println(atArgsClass.annotationMethod(new Demo("AtArgsServiceImpl.doMethod4()")) + " " + param);
    }

}