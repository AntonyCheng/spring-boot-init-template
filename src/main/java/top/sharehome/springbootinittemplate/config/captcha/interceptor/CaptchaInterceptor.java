package top.sharehome.springbootinittemplate.config.captcha.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.HandlerInterceptor;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;
import top.sharehome.springbootinittemplate.utils.redisson.rateLimit.RateLimitUtils;
import top.sharehome.springbootinittemplate.utils.redisson.rateLimit.model.TimeModel;

import java.util.concurrent.TimeUnit;

/**
 * 验证码拦截器
 *
 * @author AntonyCheng
 */
public class CaptchaInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull Object handler) throws Exception {
        // 获取请求中可能存在的IP地址
        String ipAddress = NetUtils.getIpAddressByRequest(request);
        // 除去ip地址中的所有符号
        ipAddress = ipAddress.replaceAll("\\p{P}", "");
        // 获取请求SessionId
        String sessionId = request.getSession().getId();
        // 拼接形成限流的唯一ID
        String rateLimitKey = ipAddress + sessionId;
        // 限流，只允许单个会话一秒内获取两次验证码
        RateLimitUtils.doRateLimitAndExpire(rateLimitKey, new TimeModel(1L, TimeUnit.SECONDS), 2L, 1L, new TimeModel(1L, TimeUnit.SECONDS));
        return true;
    }

}