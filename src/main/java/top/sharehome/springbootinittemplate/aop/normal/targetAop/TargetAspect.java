package top.sharehome.springbootinittemplate.aop.normal.targetAop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * Pointcut注解value参数为target型的切面类：用于匹配当前目标对象类型的执行方法；注意是目标对象的类型匹配，这样就不包括引入接口也类型匹配。
 * 特点：该类型的切面类表达式必须是精确的全类名，和this的区别就在于精确的全接口名是无法匹配的。
 * 示例：
 * "this(top.sharehome.springbootinittemplate.aop.normal.thisAop.service.impl.ThisServiceImpl)"
 * 说明：更具体的一些操作见aop.normal.executionAop。
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
public class TargetAspect {

    /**
     * 定义切入点方法
     */
    @Pointcut("this(top.sharehome.springbootinittemplate.aop.normal.thisAop.service.impl.ThisServiceImpl)")
    private void pointCutMethod() {

    }

    /**
     * 环绕通知
     *
     * @param proceedingJoinPoint 程序连接点
     * @return 返回程序执行结果
     * @throws Throwable 抛出异常
     */
    @Around("pointCutMethod()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println();
        System.out.println("环绕通知：进入方法");
        Object proceed = proceedingJoinPoint.proceed();
        System.out.println("环绕通知：退出方法");
        System.out.println();
        return proceed;
    }

    /**
     * 前置通知
     */
    @Before("pointCutMethod()")
    public void doBefore() {
        System.out.println("前置通知");
    }

    /**
     * 后置通知
     *
     * @param result 返回程序返回值
     */
    @AfterReturning(pointcut = "pointCutMethod()", returning = "result")
    public void doAfterReturning(String result) {
        System.out.println("后置通知，返回值：" + result);
    }

    /**
     * 异常通知
     *
     * @param e 异常
     */
    @AfterThrowing(pointcut = "pointCutMethod()", throwing = "e")
    public void doAfterThrowing(Exception e) {
        System.out.println("异常通知，异常：" + e.getMessage());
    }

    /**
     * 最终通知
     */
    @After("pointCutMethod()")
    public void doAfter() {
        System.out.println("最终通知");
    }

}