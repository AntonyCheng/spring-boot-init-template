package top.sharehome.springbootinittemplate.config.encrypt.interceptor.config;

import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.config.encrypt.condition.EncryptCondition;
import top.sharehome.springbootinittemplate.config.encrypt.interceptor.EncryptInterceptor;
import top.sharehome.springbootinittemplate.config.encrypt.properties.EncryptProperties;

import java.util.List;

/**
 * 请求参数加密拦截器Web配置类
 *
 * @author AntonyCheng
 */
@EnableConfigurationProperties(EncryptProperties.class)
//@Configuration
@Conditional(EncryptCondition.class)
public class EncryptInterceptorConfiguration implements WebMvcConfigurer {

    @Resource
    private EncryptProperties encryptProperties;

    @Resource
    private EncryptUriCollector encryptUriCollector;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        // 如果开启了验证码相关配置，则进行拦截
        if (encryptProperties.getEnable()) {
            List<String> encryptNeedInterceptUrl = encryptUriCollector.collectUris();
            registry.addInterceptor(new EncryptInterceptor()).addPathPatterns(encryptNeedInterceptUrl);
        }
    }

}