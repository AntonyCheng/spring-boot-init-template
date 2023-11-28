package top.sharehome.springbootinittemplate.captcha.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.captcha.service.CaptchaService;

import javax.annotation.Resource;
import java.lang.reflect.Field;

/**
 * 处理需要验证码接口的切面类
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
@Conditional(CaptchaCondition.class)
public class CaptchaAop {

    @Resource
    private CaptchaService captchaService;

    /**
     * 定义切入点方法
     */
    @Pointcut("@annotation(top.sharehome.springbootinittemplate.captcha.annotation.Captcha)")
    private void pointCutMethod() {

    }

    /**
     * 对于登录的环绕通知
     *
     * @param proceedingJoinPoint 切面连接点
     * @return 返回结果
     * @throws Throwable 抛出异常
     */
    @Around(value = "pointCutMethod()")
    public Object doAround(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        String uuid = "";
        String code = "";
        Object[] args = proceedingJoinPoint.getArgs();
        for (Object arg : args) {
            Class<?> argClass = arg.getClass();
            try {
                Field uuidField = argClass.getDeclaredField("uuid");
                uuidField.setAccessible(true);
                uuid = (String) uuidField.get(arg);
                Field codeField = argClass.getDeclaredField("code");
                codeField.setAccessible(true);
                code = (String) codeField.get(arg);
            } catch (Exception e) {
                continue;
            }
        }
        captchaService.checkCaptcha(uuid, code);
        return proceedingJoinPoint.proceed();
    }

}