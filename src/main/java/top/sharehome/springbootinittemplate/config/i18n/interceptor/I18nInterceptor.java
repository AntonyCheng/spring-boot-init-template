package top.sharehome.springbootinittemplate.config.i18n.interceptor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import top.sharehome.springbootinittemplate.config.i18n.lang_enum.LangEnum;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 国际化拦截器
 *
 * @author AntonyCheng
 */
public class I18nInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 默认为中文简体
        Locale defaultLocale = LangEnum.zh_CN.getLocale();

        // 目标语言
        Locale locale = null;

        // 获取用户传参
        String lang = StringUtils.isBlank(request.getParameter("lang")) ? "" : request.getParameter("lang");

        // 检索枚举类中是否包含该语言
        try {
            locale = LangEnum.valueOf(lang.trim()).getLocale();
        } catch (IllegalArgumentException e) {
            // 没有该语言就是用默认语言
            LocaleContextHolder.setLocale(defaultLocale);
            return HandlerInterceptor.super.preHandle(request, response, handler);
        }
        LocaleContextHolder.setLocale(locale);
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }

}