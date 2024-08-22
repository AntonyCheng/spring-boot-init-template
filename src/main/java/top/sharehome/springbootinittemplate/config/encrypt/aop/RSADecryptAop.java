package top.sharehome.springbootinittemplate.config.encrypt.aop;

import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.encrypt.EncryptConfiguration;
import top.sharehome.springbootinittemplate.config.encrypt.annotation.RSADecrypt;

/**
 * 处理需要RSA算法解密的切面类
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
public class RSADecryptAop {

    @Resource
    private EncryptConfiguration encryptConfiguration;

    /**
     * 定义切入点方法
     */
    @Pointcut("@annotation(top.sharehome.springbootinittemplate.config.encrypt.annotation.RSADecrypt)")
    private void pointCutMethod(){

    }

    /**
     * todo 前置通知
     */
    @Before("pointCutMethod()&&@annotation(rsaDecrypt)")
    public void doBefore(JoinPoint joinPoint, RSADecrypt rsaDecrypt){

    }

}
