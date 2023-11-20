package top.sharehome.springbootinittemplate.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atAnnotationAop.service.AtAnnotationService;
import top.sharehome.springbootinittemplate.aop.studyDemo.normal.beanAop.service.BeanService;

import javax.annotation.Resource;

/**
 * 切点参数为execution型的切面测试类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class ExecutionAopTest {

    @Resource
    private AtAnnotationService atAnnotationService;

    @Test
    public void testExecutionAop() {
        atAnnotationService.doMethod1();
        atAnnotationService.doMethod2();
        try {
            atAnnotationService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        atAnnotationService.doMethod4("Test");
    }

}