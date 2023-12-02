package top.sharehome.springbootinittemplate.config.captcha.interceptor.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.config.captcha.condition.CaptchaCondition;
import top.sharehome.springbootinittemplate.config.captcha.interceptor.CaptchaInterceptor;
import top.sharehome.springbootinittemplate.config.captcha.properties.CaptchaProperties;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 验证码拦截器Web配置类
 *
 * @author AntonyCheng
 */
@EnableConfigurationProperties(CaptchaProperties.class)
@Configuration
@Conditional(CaptchaCondition.class)
public class CaptchaInterceptorConfiguration implements WebMvcConfigurer {

    @Resource
    private CaptchaProperties captchaProperties;

    /**
     * 定义Captcha需要拦截的URI
     */
    private static final List<String> CAPTCHA_NEED_INTERCEPT_URI = new ArrayList<String>() {
        {
            add("/captcha");
        }
    };

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 如果开启了验证码相关配置，则进行拦截
        if (captchaProperties.isEnable()) {
            registry.addInterceptor(new CaptchaInterceptor()).addPathPatterns(CAPTCHA_NEED_INTERCEPT_URI);
        }
    }

}