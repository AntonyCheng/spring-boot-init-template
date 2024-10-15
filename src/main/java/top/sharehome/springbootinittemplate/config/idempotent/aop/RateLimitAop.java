package top.sharehome.springbootinittemplate.config.idempotent.aop;

import cn.dev33.satoken.SaManager;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.idempotent.annotation.RateLimit;
import top.sharehome.springbootinittemplate.config.idempotent.enums.ScopeType;
import top.sharehome.springbootinittemplate.config.redisson.condition.RedissonCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeRedissonException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.rateLimit.model.TimeModel;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * 处理接口限流的切面类
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
@Conditional(RedissonCondition.class)
public class RateLimitAop {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    /**
     * 定义切入点方法
     */
    @Pointcut("@annotation(top.sharehome.springbootinittemplate.config.idempotent.annotation.RateLimit)")
    private void pointCutMethod() {

    }

    /**
     * 前置通知
     */
    @Before("pointCutMethod()&&@annotation(rateLimit)")
    public void doBefore(RateLimit rateLimit) {
        if (rateLimit.time() <= 0) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "请求幂等时间必须大于0");
        }
        if (rateLimit.rate() < rateLimit.permit()) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "令牌数不足");
        }
        // 将限流单位时间统一为毫秒值
        long interval = rateLimit.timeUnit().toMillis(rateLimit.time());
        // 获取请求方法和URI
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String requestMethod = request.getMethod();
        String uri = request.getRequestURI();
        String rateLimitKey = null;
        // 判断作用范围
        if (Objects.equals(rateLimit.scopeType(), ScopeType.PERSONAL)) {
            // 获取会话请求ID
            String sessionId = request.getSession().getId();
            // 构造唯一值
            String onlyKey = DigestUtils.md5Hex(StringUtils.trimToEmpty(request.getHeader(SaManager.getConfig().getTokenName())) + ":" + sessionId);
            // 构造键（指定key + url + 唯一值）
            rateLimitKey = KeyPrefixConstants.RATE_LIMIT_ANNOTATION_PREFIX + requestMethod + ":" + uri + ":" + onlyKey;
        } else {
            // 构造键（指定key + url）
            rateLimitKey = KeyPrefixConstants.RATE_LIMIT_ANNOTATION_PREFIX + requestMethod + ":" + uri;
        }
        TimeModel rateInterval = new TimeModel(interval, TimeUnit.MILLISECONDS);
        RRateLimiter rateLimiter = REDISSON_CLIENT.getRateLimiter(rateLimitKey);
        rateLimiter.trySetRate(RateType.OVERALL, rateLimit.rate(), rateInterval.toMillis(), RateIntervalUnit.MILLISECONDS);
        boolean canOp = rateLimiter.tryAcquire(rateLimit.permit());
        if (!canOp) {
            throw new CustomizeRedissonException(ReturnCode.TOO_MANY_REQUESTS, rateLimit.message());
        }
        String expireRateLimitKey = rateLimitKey;
        List<String> uselessCacheKeys = new ArrayList<>() {
            {
                add(expireRateLimitKey);
                add("{" + expireRateLimitKey + "}:permits");
                add("{" + expireRateLimitKey + "}:value");
            }
        };
        for (String uselessCacheKey : uselessCacheKeys) {
            REDISSON_CLIENT.getBucket(uselessCacheKey).expire(Duration.ofMillis(rateInterval.toMillis()));
        }
    }

}
