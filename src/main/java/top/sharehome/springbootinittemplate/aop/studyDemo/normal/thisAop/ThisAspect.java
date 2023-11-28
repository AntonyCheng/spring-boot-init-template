package top.sharehome.springbootinittemplate.aop.studyDemo.normal.thisAop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Pointcut注解value参数为this型的切面类：用于匹配当前AOP代理对象类型的执行方法；注意是AOP代理对象的类型匹配，这样就可能包括引入接口也类型匹配。
 * 特点：该类型的切面类表达式必须是精确的全类名或者全接口名。
 * 示例：
 * "this(top.sharehome.springbootinittemplate.aop.studyDemo.normal.thisAop.service.ThisService)"
 * "this(top.sharehome.springbootinittemplate.aop.studyDemo.normal.thisAop.service.impl.ThisServiceImpl)"
 * 说明：更具体的一些操作见studyDemo.normal.executionAop。
 *
 * @author AntonyCheng
 */
//@Component
@EnableAspectJAutoProxy
@Aspect
public class ThisAspect {

    /**
     * 定义切入点方法
     */
    @Pointcut("this(top.sharehome.springbootinittemplate.aop.studyDemo.normal.thisAop.service.ThisService)")
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