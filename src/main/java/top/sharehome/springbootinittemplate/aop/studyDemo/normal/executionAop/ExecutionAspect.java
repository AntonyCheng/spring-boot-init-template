package top.sharehome.springbootinittemplate.aop.studyDemo.normal.executionAop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Pointcut注解value参数为execution型的切面类：可用于匹配精确位置的方法或者模糊位置的方法。
 * 该类型格式为："execution(方法修饰关键字 返回值类型 接入切面的方法 (方法形参类型) 异常处理)"
 * 特点：该类型的切面类必须精确到方法级别，即“接入切面的方法”的表达式最后一位一定是方法位，因此是颗粒度最细的切面类型，它能够精准定义目标方法的所有属性。
 * 相关说明如下：
 * 1、方法修饰关键字  ==> 方法修饰关键字可以忽略不写，形如 protected、private static、public static final等；
 * 2、返回值类型     ==> 返回值类型不可以忽略不写，基本类型或者Java中包含的引用类型可以直接使用，形如int、String等，如果是自定义类型，需要写清楚包名，形如top.sharehome.model.entity.User等，如果返回值对切面没影响，那么就用*表示所有返回值类型均可；
 * 3、接入切面的方法  ==> 接入切面的方法不可以忽略不写，同时要求使用execution型的切面类时需要将切入点精确到方法，比如top.sharehome.method.DemoMethod类中有demoMethod()方法，那么就需要写成top.sharehome.method.DemoMethod.demoMethod，当然也可以用*进行模糊占位处理，例如 top.sharehome.*.*.demoMethod 或 top.sharehome.*.*.* 或 top.sharehome..*.* 或 demo* 或 *；
 * 4、方法形参类型    ==> 方法形参类型不可以忽略不写，方法形参类型指的是接入切面的方法的形参类型，这里的形式参数写法和返回值类型大同小异，如果有多个，那就使用逗号隔开，形如(String)、(int,long)，如果方法形参类型对切面没影响，那么就用(..)表示任何形式的形参均可；
 * 5、异常处理       ==> 异常处理可以忽略不写，形如 throws Exception，如果异常是自定义异常，那么需要写清楚异常所在的包名
 * 综上，该类型的一个完整示例如下：
 * "execution(public static final String *..aop.studyDemo.normal.executionAop..ExecutionService.* (..) throws Exception)"
 *
 * @author AntonyCheng
 */
//@Component
@EnableAspectJAutoProxy
@Aspect
public class ExecutionAspect {

    /**
     * 定义切入点方法
     */
    @Pointcut("execution(public * *..aop.studyDemo.normal.executionAop..ExecutionService.* (..))")
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

        // 在连接点处理前获取参数信息，如果要对传入的参数进行业务处理，那么直接使用该方法即可
        Object[] args = proceedingJoinPoint.getArgs();
        if (args.length != 0) {
            args[0] = args[0] + "Test";
        }
        System.out.println("args = " + Arrays.toString(args));
        // 参数处理...

        // 在连接点处理前获取的方法签名，通过它可以获取连接点的方法名、声明类型、参数类型等信息；
        Signature signature = proceedingJoinPoint.getSignature();
        // 获取方法名
        System.out.println("signature.getName() = " + signature.getName());
        // 获取方法的修饰符整数值（详情请百度搜索Java中的常见修饰符及其对应的整数值）
        System.out.println("signature.getModifiers() = " + signature.getModifiers());
        // 获取方法所在类
        System.out.println("signature.getDeclaringType() = " + signature.getDeclaringType());
        // 获取方法所在类的类名
        System.out.println("signature.getDeclaringTypeName() = " + signature.getDeclaringTypeName());

        // 获取连接点的种类（详情请百度搜索Java中常见的连接点种类）
        String kind = proceedingJoinPoint.getKind();
        System.out.println("kind = " + kind);

        // 获取目标对象
        Object target = proceedingJoinPoint.getTarget();
        System.out.println("target = " + target);

        // 获取代理对象
        Object aThis = proceedingJoinPoint.getThis();
        System.out.println("aThis = " + aThis);

        // 连接点处理（如果在连接点执行前修改过参数，需要将参数传入该方法中）
        Object proceed = proceedingJoinPoint.proceed(args);
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