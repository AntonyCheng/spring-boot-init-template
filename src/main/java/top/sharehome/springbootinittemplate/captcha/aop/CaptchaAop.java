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
import top.sharehome.springbootinittemplate.model.dto.auth.AuthLoginDto;
import top.sharehome.springbootinittemplate.model.dto.auth.AuthRegisterDto;

import javax.annotation.Resource;

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
     */
    @Around(value = "pointCutMethod()&&args(authLoginDto))")
    public Object doAroundLogin(ProceedingJoinPoint proceedingJoinPoint, AuthLoginDto authLoginDto) throws Throwable {
        captchaService.checkCaptcha(authLoginDto.getCode(), authLoginDto.getUuid());
        Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }

    /**
     * 对于注册的环绕通知
     */
    @Around(value = "pointCutMethod()&&args(authRegisterDto)")
    public Object doAroundRegister(ProceedingJoinPoint proceedingJoinPoint, AuthRegisterDto authRegisterDto) throws Throwable {
        captchaService.checkCaptcha(authRegisterDto.getCode(), authRegisterDto.getUuid());
        Object proceed = proceedingJoinPoint.proceed();
        return proceed;
    }

}