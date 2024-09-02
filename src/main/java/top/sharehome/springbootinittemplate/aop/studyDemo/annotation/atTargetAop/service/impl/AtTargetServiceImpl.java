package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atTargetAop.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atTargetAop.annotation.AtTargetClass;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atTargetAop.service.AtTargetService;

/**
 * 针对切点参数为@target型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class AtTargetServiceImpl implements AtTargetService {

    @Resource
    private AtTargetClass atTargetClass;

    @Override
    public void doMethod1() {
        System.out.println(atTargetClass.annotationMethod("AtTargetServiceImpl.doMethod1()"));
    }

    @Override
    public String doMethod2() {
        return atTargetClass.annotationMethod("AtTargetServiceImpl.doMethod2()");
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println(atTargetClass.annotationMethod("AtTargetServiceImpl.doMethod3()"));
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String param) {
        System.out.println(atTargetClass.annotationMethod("AtTargetServiceImpl.doMethod4()") + " " + param);
    }

}