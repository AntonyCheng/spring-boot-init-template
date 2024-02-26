package top.sharehome.springbootinittemplate.config.i18n.interceptor.config;

import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.config.caffeine.properties.CaffeineProperties;
import top.sharehome.springbootinittemplate.config.i18n.condition.I18nCondition;
import top.sharehome.springbootinittemplate.config.i18n.interceptor.I18nInterceptor;
import top.sharehome.springbootinittemplate.config.i18n.properties.I18nProperties;

/**
 * 国际化拦截器Web配置类
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(I18nProperties.class)
@AllArgsConstructor
@Conditional(I18nCondition.class)
public class I18nInterceptorConfiguration implements WebMvcConfigurer {

    private final I18nProperties i18nProperties;

    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(new I18nInterceptor(i18nProperties.getDefaultLocale())).addPathPatterns("/**");
    }

}