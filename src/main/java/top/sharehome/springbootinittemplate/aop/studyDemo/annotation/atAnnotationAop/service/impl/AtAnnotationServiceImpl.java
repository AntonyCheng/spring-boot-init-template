package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop.service.impl;

import org.springframework.stereotype.Service;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationClass;
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
    private AnnotationClass annotationClass;

    @Override
    public void doMethod1() {
        System.out.println(annotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod1()"));
    }

    @Override
    public String doMethod2() {
        return annotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod2()");
    }

    @Override
    public void doMethod3() throws Exception {
        System.out.println(annotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod3()"));
        throw new Exception("some exception");
    }

    @Override
    public void doMethod4(String demo) {
        System.out.println(annotationClass.annotationMethod("AtAnnotationServiceImpl.doMethod3()") + " " + demo);
    }

}