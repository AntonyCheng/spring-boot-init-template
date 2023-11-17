package top.sharehome.springbootinittemplate.aop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.aop.normal.executionAop.service.ExecutionService;

import javax.annotation.Resource;

/**
 * 切点参数为execution型的切面测试类
 *
 * @author AntonyCheng
 * @since 2023/11/16 22:17:32
 */
@SpringBootTest
public class ExecutionAopTest {

    @Resource
    private ExecutionService executionService;

    @Test
    public void testExecutionAop() {
        executionService.doMethod1();
        executionService.doMethod2();
        try {
            executionService.doMethod3();
        } catch (Exception e) {
            System.out.println("抓住异常");
        }
        executionService.doMethod4("demo");
    }

}