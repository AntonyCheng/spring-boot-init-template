package top.sharehome.springbootinittemplate.config.security;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.filter.SaServletFilter;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.util.SaResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import top.sharehome.springbootinittemplate.captcha.properties.CaptchaProperties;
import top.sharehome.springbootinittemplate.common.base.HttpStatus;
import top.sharehome.springbootinittemplate.config.security.condition.IdentificationCondition;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * SaToken认证配置
 *
 * @author AntonyCheng
 */
@Configuration
@EnableConfigurationProperties(CaptchaProperties.class)
@Conditional(IdentificationCondition.class)
@Slf4j
public class IdentificationConfiguration implements WebMvcConfigurer {

    /**
     * 定义SaToken不需要拦截的URI
     */
    private static final List<String> SA_TOKEN_NOT_NEED_INTERCEPT_URI = new ArrayList<String>() {
        {
            add("/auth/register");
            add("/auth/login");
            add("/captcha");
        }
    };

    /**
     * 注册sa-token的拦截器
     *
     * @param registry 拦截器注册器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册路由拦截器，自定义验证规则
        registry.addInterceptor(new SaInterceptor()).addPathPatterns("/**").excludePathPatterns(SA_TOKEN_NOT_NEED_INTERCEPT_URI);
    }

    /**
     * 校验是否从网关转发
     */
    @Bean
    public SaServletFilter getSaServletFilter() {
        return new SaServletFilter()
                // 指定拦截的路由
                .addInclude("/**")
                // 排除不需要拦截的资源 ---- 这里可以自定义
                .addExclude("/favicon.ico", "/actuator/**")
                // 认证函数
                .setAuth(obj -> {
                    // 如果想在过滤器器层面做认证，请将逻辑代码编写到此处
                })
                // 异常处理函数：每次认证函数发生异常时执行此函数
                .setError(e -> SaResult.error("认证失败，无法访问系统资源").setCode(HttpStatus.UNAUTHORIZED))
                .setBeforeAuth(r -> {
                    // ---------- 设置一些安全响应头 ----------
                    SaHolder.getResponse()
                            // 服务器名称
                            .setServer("antony-server")
                            // 是否可以在iframe显示视图： DENY=不可以 | SAMEORIGIN=同域下可以 | ALLOW-FROM uri=指定域名下可以
                            .setHeader("X-Frame-Options", "SAMEORIGIN")
                            // 是否启用浏览器默认XSS防护： 0=禁用 | 1=启用 | 1; mode=block 启用, 并在检查到XSS攻击时，停止渲染页面
                            .setHeader("X-XSS-Protection", "1; mode=block")
                            // 禁用浏览器内容嗅探
                            .setHeader("X-Content-Type-Options", "nosniff");
                });
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ identification config DI.");
    }

}