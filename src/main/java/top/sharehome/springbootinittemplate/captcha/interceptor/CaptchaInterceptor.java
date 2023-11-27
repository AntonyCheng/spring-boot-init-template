package top.sharehome.springbootinittemplate.captcha.interceptor;

import lombok.AllArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;
import top.sharehome.springbootinittemplate.captcha.service.CaptchaService;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;
import top.sharehome.springbootinittemplate.utils.redisson.RateLimitUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
        // 获取请求中可能存在的IP地址
        String ipAddress = NetUtils.getIpAddressByRequest(request);
        // 除去ip地址中的所有符号
        ipAddress = ipAddress.replaceAll("\\p{P}", "");
        // 获取请求SessionId
        String sessionId = request.getSession().getId();
        // 拼接形成限流的唯一ID
        String rateLimitKey = ipAddress + sessionId;
        RateLimitUtils.doRateLimitAndExpire(rateLimitKey);
        return true;
    }

}