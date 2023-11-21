package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop.service.impl;

import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop.annotation.AtAnnotationClass;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop.service.AtAnnotationService;

import javax.annotation.Resource;

/**
 * 针对切点参数为@annotation型的切面类的服务实现类
 *
 * @author AntonyCheng
 */
@Service
public class AtAnnotationServiceImpl implements AtAnnotationService {

    @Resource
    private AtAnnotationClass atAnnotationClass;

    @Override
    public void doMethod1() {
        System.out.println(atAnnotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod1()"));
    }

    @Override
    public String doMethod2() {
        return atAnnotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod2()");
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println(atAnnotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod3()"));
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String param) {
        System.out.println(atAnnotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod4()") + " " + param);
    }

}