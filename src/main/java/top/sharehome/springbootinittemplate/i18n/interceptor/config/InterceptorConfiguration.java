package top.sharehome.springbootinittemplate.i18n.interceptor.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.i18n.interceptor.LocaleInterceptor;

/**
 * 拦截器Web配置类
 *
 * @author AntonyCheng
 */
@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(new LocaleInterceptor()).addPathPatterns("/i18n");
    }

}