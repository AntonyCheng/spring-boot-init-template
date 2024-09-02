package top.sharehome.springbootinittemplate.config.idempotent.aop;

import cn.dev33.satoken.SaManager;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.idempotent.annotation.PreventRepeat;
import top.sharehome.springbootinittemplate.config.idempotent.enums.ScopeType;
import top.sharehome.springbootinittemplate.config.redisson.condition.RedissonCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.cache.CacheUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.util.Objects;

/**
 * 处理接口防重的切面类
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
@Conditional(RedissonCondition.class)
public class PreventRepeatAop {

    /**
     * 记录缓存键
     */
    private static final ThreadLocal<String> CACHE_KEY_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 定义切入点方法
     */
    @Pointcut("@annotation(top.sharehome.springbootinittemplate.config.idempotent.annotation.PreventRepeat)")
    private void pointCutMethod() {

    }

    /**
     * 前置通知
     */
    @Before("pointCutMethod()&&@annotation(preventRepeat)")
    public void doBefore(PreventRepeat preventRepeat) {
        if (preventRepeat.time() <= 0) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "防重单位时间必须大于0");
        }
        // 将防重单位时间统一为毫秒值
        long interval = preventRepeat.timeUnit().toMillis(preventRepeat.time());
        // 获取请求方法和URI
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String requestMethod = request.getMethod();
        String uri = request.getRequestURI();
        String preventRepeatKey = null;
        // 判断作用范围
        if (Objects.equals(preventRepeat.scopeType(), ScopeType.PERSONAL)) {
            // 获取会话请求ID
            String sessionId = request.getSession().getId();
            // 构造唯一值
            String onlyKey = DigestUtils.md5Hex(StringUtils.trimToEmpty(request.getHeader(SaManager.getConfig().getTokenName())) + ":" + sessionId);
            // 构造键（指定key + url + 唯一值）
            preventRepeatKey = KeyPrefixConstants.PREVENT_REPEAT + requestMethod + ":" + uri + ":" + onlyKey;
        } else {
            // 构造键（指定key + url）
            preventRepeatKey = KeyPrefixConstants.PREVENT_REPEAT + requestMethod + ":" + uri;
        }
        if (CacheUtils.putNoPrefixIfAbsent(preventRepeatKey, "", Duration.ofMillis(interval))) {
            CACHE_KEY_THREAD_LOCAL.set(preventRepeatKey);
        } else {
            throw new CustomizeReturnException(ReturnCode.TOO_MANY_REQUESTS, preventRepeat.message());
        }
    }

    /**
     * 后置通知
     *
     * @param returnResult 响应结果
     */
    @AfterReturning(pointcut = "pointCutMethod()", returning = "returnResult")
    public void doAfterReturning(Object returnResult) {
        try {
            if (Objects.isNull(returnResult) || (returnResult instanceof R<?> && ((R<?>) returnResult).getCode() == R.SUCCESS)) {
                return;
            }
            CacheUtils.deleteNoPrefix(CACHE_KEY_THREAD_LOCAL.get());
        } finally {
            CACHE_KEY_THREAD_LOCAL.remove();
        }
    }

    /**
     * 异常通知
     */
    @AfterThrowing(pointcut = "pointCutMethod()")
    public void doAfterThrowing() {
        CacheUtils.deleteNoPrefix(CACHE_KEY_THREAD_LOCAL.get());
        CACHE_KEY_THREAD_LOCAL.remove();
    }

}
