package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.service.impl;

import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.annotation.AtWithinSonClass;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.service.AtWithinService;

/**
 * 针对切点参数为@within型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class AtWithinServiceImpl implements AtWithinService {

    @Resource
    private AtWithinSonClass atWithinSonClass;

    @Override
    public void doMethod1() {
        System.out.println(atWithinSonClass.annotationMethod("AtWithinServiceImpl.doMethod1()"));
    }

    @Override
    public String doMethod2() {
        return atWithinSonClass.annotationMethod("AtWithinServiceImpl.doMethod2()");
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println(atWithinSonClass.annotationMethod("AtWithinServiceImpl.doMethod3()"));
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String param) {
        System.out.println(atWithinSonClass.annotationMethod("AtWithinServiceImpl.doMethod4()") + " " + param);
    }

}