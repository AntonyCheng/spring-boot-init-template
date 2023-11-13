package top.sharehome.springbootinittemplate.config.security;

import cn.dev33.satoken.SaManager;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.same.SaSameUtil;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.common.base.HttpStatus;
import top.sharehome.springbootinittemplate.config.security.condition.IdentificationCondition;

import javax.annotation.PostConstruct;

/**
 * SaToken认证配置
 *
 * @author AntonyCheng
 */
@Configuration
@Conditional(IdentificationCondition.class)
@Slf4j
public class IdentificationConfiguration implements WebMvcConfigurer {

    /**
     * 注册sa-token的拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**");
    }

    /**
     * 校验是否从网关转发
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                .addInclude("/**")
                // 排除前端资源 ---- 这里可以自定义
                // .addExclude("/front/**")
                .setAuth(obj -> {
                    if (SaManager.getConfig().getCheckSameToken()) {
                        SaSameUtil.checkCurrentRequestToken();
                    }
                })
                .setError(e -> SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED));
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ identification config DI.");
    }

}