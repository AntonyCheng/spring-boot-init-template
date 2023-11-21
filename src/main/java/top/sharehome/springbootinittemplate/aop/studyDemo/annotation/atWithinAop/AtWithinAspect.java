package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

/**
 * Pointcut注解value参数为@within型的切面类：用于匹配带有指定注解的类及其子类中的方法。
 * 特点：通常来说需要搭配execution型或者其他可以定位包的切面表达式一起使用，确定好包之后再去确定@args表达式的内容，区别于@annotation型的切面类，@within型的切面类颗粒度大一点，精确到类，区别于@target型的切面类会影响被注解类的子类。详情请查看相关示例代码。
 * 说明：如果想要使用该型切面涉及到的指定注解的类子类继承的特性，那所指定的注解一定要有@Inherited注解。
 *
 * @author AntonyCheng
 */
//@Component
@EnableAspectJAutoProxy
@Aspect
public class AtWithinAspect {

    /**
     * 定义切入点方法
     */
    @Pointcut("execution(* *..aop..atWithinAop..* (..)) && @within(top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atWithinAop.annotation.AtWithinAop)")
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