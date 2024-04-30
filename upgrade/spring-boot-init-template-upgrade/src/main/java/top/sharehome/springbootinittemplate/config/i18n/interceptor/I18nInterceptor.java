package top.sharehome.springbootinittemplate.config.i18n.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import top.sharehome.springbootinittemplate.config.i18n.properties.enums.LocaleType;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 国际化拦截器
 *
 * @author AntonyCheng
 */
public class I18nInterceptor implements HandlerInterceptor {

    private Locale defaultLocale = null;

    public I18nInterceptor(LocaleType defaultLocale) {
        this.defaultLocale = defaultLocale.getLocale();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 目标语言
        Locale locale = null;

        // 获取用户传参
        String lang = StringUtils.isBlank(request.getParameter("lang")) ? "" : request.getParameter("lang");

        // 判断请求中是否带有非空参数
        if (StringUtils.isBlank(lang)) {
            LocaleContextHolder.setLocale(defaultLocale);
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }

        // 检索枚举类中是否包含该语言
        try {
            locale = LocaleType.valueOf(StringUtils.upperCase(lang).trim()).getLocale();
        } catch (IllegalArgumentException e) {
            // 没有该语言就是用默认语言
            LocaleContextHolder.setLocale(defaultLocale);
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        LocaleContextHolder.setLocale(locale);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}