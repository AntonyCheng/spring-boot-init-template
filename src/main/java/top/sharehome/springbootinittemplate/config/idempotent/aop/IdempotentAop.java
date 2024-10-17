package top.sharehome.springbootinittemplate.config.idempotent.aop;

import cn.dev33.satoken.SaManager;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.idempotent.annotation.Idempotent;
import top.sharehome.springbootinittemplate.config.redisson.condition.RedissonCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeRedissonException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.cache.CacheUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.time.Duration;
import java.util.*;

/**
 * 处理请求幂等的切面类
 * 💡源自美团GTIS解决方案（https://tech.meituan.com/2016/09/29/distributed-system-mutually-exclusive-idempotence-cerberus-gtis.html）
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
@Conditional(RedissonCondition.class)
public class IdempotentAop {

    /**
     * 记录缓存键
     */
    private static final ThreadLocal<String> CACHE_KEY_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 定义切入点方法
     */
    @Pointcut("@annotation(top.sharehome.springbootinittemplate.config.idempotent.annotation.Idempotent)")
    private void pointCutMethod() {

    }

    /**
     * 前置通知
     */
    @Before("pointCutMethod()&&@annotation(idempotent)")
    public void doBefore(JoinPoint joinPoint, Idempotent idempotent) {
        if (idempotent.time() <= 0) {
            throw new CustomizeReturnException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "请求幂等时间必须大于0");
        }
        // 将请求幂等时间统一为毫秒值
        long interval = idempotent.timeUnit().toMillis(idempotent.time());
        // 获取请求方法和URI
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (Objects.isNull(servletRequestAttributes)) {
            throw new CustomizeReturnException(ReturnCode.USER_SENT_INVALID_REQUEST, "无法获取请求");
        }
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String requestMethod = request.getMethod();
        String uri = request.getRequestURI();
        // 获取请求参数
        Map<String, String[]> parameter = Collections.unmodifiableMap(request.getParameterMap());
        Map<String, String> parameterMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : parameter.entrySet()) {
            parameterMap.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
        }
        String param = null;
        // 只对原本请求参数为空的POST类型请求进行请求体参数内容的获取
        if (MapUtils.isEmpty(parameterMap) && (HttpMethod.POST.name().equals(requestMethod) || HttpMethod.PUT.name().equals(requestMethod))) {
            StringJoiner stringJoiner = new StringJoiner(",");
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (Objects.nonNull(arg) && !isFilterObject(arg)) {
                    TypeReference<HashMap<String, Object>> type = new TypeReference<>() {
                    };
                    HashMap<String, Object> map = JSON.parseObject(JSON.toJSONString(arg), type);
                    stringJoiner.add(JSON.toJSONString(map));
                }
            }
            param = stringJoiner.toString();
        } else {
            param = JSON.toJSONString(parameterMap);
        }
        // 构造唯一值
        String onlyKey = DigestUtils.md5Hex(StringUtils.trimToEmpty(request.getHeader(SaManager.getConfig().getTokenName())) + ":" + param);
        // 唯一标识（指定key + url + 唯一值）
        String idempotentKey = KeyPrefixConstants.IDEMPOTENT_PREFIX + requestMethod + ":" + uri + ":" + onlyKey;
        if (CacheUtils.putNoPrefixIfAbsent(idempotentKey, "", Duration.ofMillis(interval))) {
            CACHE_KEY_THREAD_LOCAL.set(idempotentKey);
        } else {
            throw new CustomizeRedissonException(ReturnCode.TOO_MANY_REQUESTS, idempotent.message());
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
            if (Objects.isNull(returnResult) || (returnResult instanceof R<?> r && r.getCode() == R.SUCCESS)) {
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

    /**
     * 过滤掉不可用JSON表达的参数和一些Http访问参数
     *
     * @param obj 校验过滤对象
     */
    @SuppressWarnings("rawtypes")
    private boolean isFilterObject(Object obj) {
        Class<?> clazz = obj.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) obj;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) obj;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        } else {
            for (Field declaredField : clazz.getDeclaredFields()) {
                try {
                    int modifiers = declaredField.getModifiers();
                    // 如果是静态属性或者final属性则不进行判断
                    if (Modifier.isStatic(modifiers)
                            || Modifier.isFinal(modifiers)) {
                        continue;
                    }
                    declaredField.setAccessible(true);
                    Object object = declaredField.get(obj);
                    if (object instanceof MultipartFile
                            || object instanceof HttpServletRequest
                            || object instanceof HttpServletResponse
                            || object instanceof BindingResult) {
                        return true;
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return obj instanceof MultipartFile || obj instanceof HttpServletRequest || obj instanceof HttpServletResponse || obj instanceof BindingResult;
    }

}
