package top.sharehome.springbootinittemplate.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop.service.AtAnnotationService;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop.service.AtArgsService;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atTargetAop.service.AtTargetService;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.service.AtWithinService;

import javax.annotation.Resource;

/**
 * 切点参数为execution型的切面测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class AnnotationAopTest {

    @Resource
    private AtAnnotationService atAnnotationService;

    @Resource
    private AtArgsService atArgsService;

    @Resource
    private AtTargetService atTargetService;

    @Resource
    private AtWithinService atWithinService;

    @Test
    public void testAtAnnotationAop() {
        atAnnotationService.doMethod1();
        atAnnotationService.doMethod2();
        try {
            atAnnotationService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        atAnnotationService.doMethod4("Test");
    }

    @Test
    public void testAtArgsAop() {
        atArgsService.doMethod1();
        atArgsService.doMethod2();
        try {
            atArgsService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        atArgsService.doMethod4("Test");
    }

    @Test
    public void testAtTargetAop() {
        atTargetService.doMethod1();
        atTargetService.doMethod2();
        try {
            atTargetService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        atTargetService.doMethod4("Test");
    }

    @Test
    public void testAtWithinAop() {
        atWithinService.doMethod1();
        atWithinService.doMethod2();
        try {
            atWithinService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        atWithinService.doMethod4("Test");
    }

}