package top.sharehome.springbootinittemplate.aop.studyDemo.annotation.atArgsAop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationAop;

/**
 * Pointcut注解value参数为@annotation型的切面类：用于匹配带有指定注解的方法。
 * 特点：该类型注解可以写全类名，也可以写注解接口形参名，前者用于确定连接点位置，后者用于传递数据到切面方法中，两者可以分开用，也可以搭配用，其实Spring AOP中很多其他类型的切面类也有该特性，只不过在注解中用的最多。
 * 示例：
 * "@annotation(top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationAop)"
 * "@annotation(annotationAop)"
 * "@annotation(top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationAop)&&@annotation(annotationAop)"
 * 说明：通常写注解接口形参名这种方式不会写在@Pointcut注解中，一般写在@Around等注解中，因为@Pointcut注解只定义切入点方法，不处理业务逻辑。
 * 示例：@Around(value = "@annotation(top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationAop)&&@annotation(annotationAop)")
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
public class AtArgsAspect {

    /**
     * 定义切入点方法
     */
    @Pointcut("@args(top.sharehome.springbootinittemplate.aop.studyDemo.annotation.annotationDemo.AnnotationAop)")
    private void pointCutMethod() {

    }

    /**
     * 环绕通知
     *
     * @param proceedingJoinPoint 程序连接点
     * @param annotationAop       由 && 与逻辑传入到切面方法中的连接点注解信息
     * @return 返回程序执行结果
     * @throws Throwable 抛出异常
     */
    @Around(value = "pointCutMethod()&&@annotation(annotationAop)")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint, AnnotationAop annotationAop) throws Throwable {
        System.out.println();
        System.out.println("环绕通知：进入方法");
        System.out.println("annotationAop = " + annotationAop);
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