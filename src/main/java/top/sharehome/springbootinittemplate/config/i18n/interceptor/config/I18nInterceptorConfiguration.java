package top.sharehome.springbootinittemplate.config.i18n.interceptor.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.config.i18n.condition.I18nCondition;
import top.sharehome.springbootinittemplate.config.i18n.interceptor.I18nInterceptor;

/**
 * 国际化拦截器Web配置类
 *
 * @author AntonyCheng
 */
@Configuration
@Conditional(I18nCondition.class)
public class I18nInterceptorConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(new I18nInterceptor()).addPathPatterns("/**");
    }

}