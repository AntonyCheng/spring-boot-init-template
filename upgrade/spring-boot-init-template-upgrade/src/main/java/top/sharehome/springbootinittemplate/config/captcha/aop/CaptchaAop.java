package top.sharehome.springbootinittemplate.config.captcha.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.config.captcha.model.Captcha;
import top.sharehome.springbootinittemplate.config.captcha.service.CaptchaService;

import jakarta.annotation.Resource;
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
    @Pointcut("@annotation(top.sharehome.springbootinittemplate.config.captcha.annotation.EnableCaptcha)")
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
                Field captchaField = argClass.getDeclaredField("captcha");
                captchaField.setAccessible(true);
                Captcha captcha = (Captcha) captchaField.get(arg);
                uuid = captcha.getUuid();
                code = captcha.getCode();
            } catch (Exception ignored) {
            }
        }
        captchaService.checkCaptcha(code, uuid);
        return proceedingJoinPoint.proceed();
    }

}