package top.sharehome.springbootinittemplate.captcha.interceptor.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.captcha.interceptor.CaptchaInterceptor;
import top.sharehome.springbootinittemplate.captcha.properties.CaptchaProperties;
import top.sharehome.springbootinittemplate.captcha.service.CaptchaService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器Web配置类
 *
 * @author AntonyCheng
 */
@EnableConfigurationProperties(CaptchaProperties.class)
@Configuration
@Conditional(CaptchaCondition.class)
public class InterceptorConfiguration implements WebMvcConfigurer {
    @Resource
    private CaptchaProperties captchaProperties;

    @Resource
    private CaptchaService captchaService;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 定义Captcha需要拦截的URI
        List<String> captchaNeedInterceptUri = new ArrayList<String>() {
            {
                add("/captcha");
            }
        };
        // 如果开启了验证码相关配置，则进行拦截
        if (captchaProperties.isEnable()) {
            registry.addInterceptor(new CaptchaInterceptor(captchaService)).addPathPatterns(captchaNeedInterceptUri);
        }
    }

}