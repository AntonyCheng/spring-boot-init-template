package top.sharehome.springbootinittemplate.config.captcha.aop;

import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.captcha.annotation.EnableCaptcha;
import top.sharehome.springbootinittemplate.config.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.config.captcha.model.Captcha;
import top.sharehome.springbootinittemplate.config.captcha.service.CaptchaService;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;

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
     * 前置通知
     */
    @Before("pointCutMethod()")
    public void doBefore(JoinPoint joinPoint) {
        String uuid = "";
        String code = "";
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            Class<?> argClass = arg.getClass();
            try {
                Field captchaField = argClass.getDeclaredField("captcha");
                captchaField.setAccessible(true);
                Captcha captcha = (Captcha) captchaField.get(arg);
                uuid = captcha.getUuid();
                code = captcha.getCode();
            } catch (Exception e) {
                throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH,"缺少验证码");
            }
        }
        captchaService.checkCaptcha(code, uuid);
    }

}