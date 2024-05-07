package top.sharehome.springbootinittemplate.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.argsAop.service.ArgsService;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.beanAop.service.BeanService;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.executionAop.service.ExecutionService;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.targetAop.service.TargetService;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.thisAop.service.ThisService;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.withinAop.service.WithinService;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.withinAop.service.impl.WithinServiceImpl1;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.withinAop.service.impl.WithinServiceImpl2;

import jakarta.annotation.Resource;

/**
 * 切点参数为execution型的切面测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class NormalAopTest {

    @Resource
    private ExecutionService executionService;

    @Resource(type = WithinServiceImpl1.class)
    private WithinService withinServiceImpl1;

    @Resource(type = WithinServiceImpl2.class)
    private WithinService withinServiceImpl2;

    @Resource
    private ArgsService argsService;

    @Resource
    private TargetService targetService;

    @Resource
    private ThisService thisService;

    @Resource
    private BeanService beanService;

    /**
     * 测试execution型切面类
     */
    @Test
    public void testExecutionAop() {
        executionService.doMethod1();
        executionService.doMethod2();
        try {
            executionService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        executionService.doMethod4("Test");
    }

    /**
     * 测试within型切面类
     */
    @Test
    public void testWithinAop() {
        withinServiceImpl1.doMethod1();
        withinServiceImpl2.doMethod1();
        withinServiceImpl1.doMethod2();
        withinServiceImpl2.doMethod2();
        try {
            withinServiceImpl1.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        try {
            withinServiceImpl2.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        withinServiceImpl1.doMethod4("Test");
        withinServiceImpl2.doMethod4("Test");
    }

    /**
     * 测试args型切面类
     */
    @Test
    public void testArgsAop() {
        argsService.doMethod1();
        argsService.doMethod2();
        try {
            argsService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        argsService.doMethod4("Test");
    }

    /**
     * 测试target型切面类
     */
    @Test
    public void testTargetAop() {
        targetService.doMethod1();
        targetService.doMethod2();
        try {
            targetService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        targetService.doMethod4("Test");
    }

    /**
     * 测试this型切面类
     */
    @Test
    public void testThisAop() {
        thisService.doMethod1();
        thisService.doMethod2();
        try {
            thisService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        thisService.doMethod4("Test");
    }

    /**
     * 测试bean型切面类
     */
    @Test
    public void testBeanAop() {
        beanService.doMethod();
    }

}