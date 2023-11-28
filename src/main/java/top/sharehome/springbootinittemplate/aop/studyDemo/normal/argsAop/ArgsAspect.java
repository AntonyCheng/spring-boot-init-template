package top.sharehome.springbootinittemplate.aop.studyDemo.normal.argsAop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * Pointcut注解value参数为args型的切面类：用于匹配参数类型符合指定类型的方法。
 * 特点：该类就负责指定类型参数类型，大多数Java自带的类型都能够直接使用，但是如果要指定自定义类型，那么就需要写该类的全类名。
 * 示例：
 * "args(String)"
 * "args(String,int)"
 * "args(String,top.sharehome.springbootinittemplate.model.entity.User)""
 * 说明：一般来说，使用AOP单单指定参数类型是不能确定连接点的，需要搭配类（within）一起使用，所以这里可以使用&&、||、!:三种逻辑（与或非）连接符号进行表达式的连接，更具体的一些操作见studyDemo.normal.executionAop。
 * 示例：
 * "within(*..aop.studyDemo.normal.withinAop..*.*)&&args(String)"
 *
 * @author AntonyCheng
 */
//@Component
@EnableAspectJAutoProxy
@Aspect
public class ArgsAspect {

    /**
     * 定义切入点方法
     */
    @Pointcut("within(*..aop.studyDemo.normal.argsAop..*.*)&&args(String)")
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