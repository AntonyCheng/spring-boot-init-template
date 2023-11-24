package top.sharehome.springbootinittemplate.config.captcha.interceptor;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import top.sharehome.springbootinittemplate.config.captcha.service.CaptchaService;
import top.sharehome.springbootinittemplate.utils.redisson.RateLimitUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 验证码拦截器
 *
 * @author AntonyCheng
 */
@AllArgsConstructor
public class CaptchaInterceptor implements HandlerInterceptor {

    private final CaptchaService captchaService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String servletPath = request.getServletPath();
        if (StringUtils.equals(servletPath,"/captcha")){
//            RateLimitUtils.doRateLimit();
        }
        System.out.println(request.getSession().getId());
        return true;
    }

}