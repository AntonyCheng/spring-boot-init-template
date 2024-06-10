package top.sharehome.springbootinittemplate.config.log.aop;

import com.alibaba.fastjson2.JSON;
import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.mapper.LogMapper;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.util.*;

/**
 * 日志的切面类
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
@Slf4j
public class ControllerLogAop {

    /**
     * 获取LogMapper
     */
    private static final LogMapper LOG_MAPPER = SpringContextHolder.getBean(LogMapper.class);

    /**
     * 系统默认屏蔽的请求/响应字段
     * User：password、checkPassword、oldPassword、newPassword、checkNewPassword
     */
    private static final String[] MASK_PARAMS = {"password", "checkPassword", "oldPassword", "newPassword", "newPassword", "checkNewPassword", "captcha", "token"};

    /**
     * 记录日志操作用户ID
     */
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 计算日志操作访问耗时，TransmittableThreadLocal是Alibaba继承ThreadLocal的一个类，它适用于适用于复杂的线程池、异步任务等场景，由于该日志记录过程中可能会存在这些场景，所以能够保证线程本地变量的传递性。
     */
    private static final ThreadLocal<StopWatch> COST_TIME_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 定义切入点方法
     */
    @Pointcut("@annotation(top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog)")
    private void pointCutMethod() {

    }

    /**
     * 前置通知
     */
    @Before("pointCutMethod()")
    public void doBefore() {
        try {
            USER_ID_THREAD_LOCAL.set(LoginUtils.getLoginUserId());
        } catch (Exception e) {
            USER_ID_THREAD_LOCAL.remove();
        }
        StopWatch stopWatch = new StopWatch();
        COST_TIME_THREAD_LOCAL.set(stopWatch);
        stopWatch.start();
    }

    /**
     * 后置通知
     *
     * @param joinPoint     切点
     * @param controllerLog 注解
     * @param returnResult  响应
     */
    @AfterReturning(value = "pointCutMethod()&&@annotation(controllerLog)", returning = "returnResult")
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void doAfterReturning(JoinPoint joinPoint, ControllerLog controllerLog, R returnResult) {
        try {
            Log log = new Log();
            // 设置操作参数
            StringJoiner stringJoiner = new StringJoiner(",");
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (Objects.nonNull(arg) && !isFilterObject(arg)) {
                    Map map = JSON.parseObject(JSON.toJSONString(arg), Map.class);
                    if (MapUtils.isNotEmpty(map)) {
                        Arrays.stream(MASK_PARAMS).forEach(map::remove);
                    }
                    stringJoiner.add(JSON.toJSONString(map));
                }
            }
            log.setParam(stringJoiner.toString());
            // 设置操作方法名称
            String clazz = joinPoint.getTarget().getClass().getName();
            String method = joinPoint.getSignature().getName();
            log.setMethod(clazz + "." + method);
            // 设置操作描述
            log.setDescription(controllerLog.description());
            // 设置操作类型
            log.setOperator(controllerLog.operator().getOperatorValue());
            // 设置操作结果
            log.setResult(returnResult.getCode() == 200 ? 0 : 1);
            // 设置响应内容
            Map resMap = JSON.parseObject(JSON.toJSONString(returnResult), Map.class);
            Map dataMap = (Map) resMap.get("data");
            if (Objects.nonNull(dataMap)) {
                Arrays.stream(MASK_PARAMS).forEach(dataMap::remove);
            }
            resMap.put("data", dataMap);
            log.setJson(JSON.toJSONString(resMap));
            // 设置操作用户ID
            Long userId = null;
            try {
                userId = LoginUtils.getLoginUserId();
            } catch (Exception ignored) {
            }
            log.setUserId(Objects.isNull(userId) ? USER_ID_THREAD_LOCAL.get() : userId);
            // 设置接口URI和请求方法类型
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            log.setUri(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
            // 设置操作用户的IP和地址
            log.setIp(NetUtils.getIpAddressByRequest(request));
            log.setLocation(NetUtils.getRegionByRequest(request));
            // 设置接口访问耗时
            StopWatch stopWatch = COST_TIME_THREAD_LOCAL.get();
            stopWatch.stop();
            long time = stopWatch.getTime();
            log.setTime(time);
            // 插入数据库
            LOG_MAPPER.insert(log);
        } catch (Exception exception) {
            log.error("日志记录报错：{}", exception.getMessage());
            exception.printStackTrace();
        } finally {
            USER_ID_THREAD_LOCAL.remove();
            COST_TIME_THREAD_LOCAL.remove();
        }
    }

    /**
     * 异常通知
     *
     * @param joinPoint     切点
     * @param controllerLog 注解
     * @param e             异常
     */
    @AfterThrowing(value = "pointCutMethod()&&@annotation(controllerLog)", throwing = "e")
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void doAfterThrowing(JoinPoint joinPoint, ControllerLog controllerLog, final Throwable e) {
        try {
            Log log = new Log();
            // 设置操作参数
            StringJoiner stringJoiner = new StringJoiner(",");
            Object[] args = joinPoint.getArgs();
            for (Object arg : args) {
                if (Objects.nonNull(arg) && !isFilterObject(arg)) {
                    Map map = JSON.parseObject(JSON.toJSONString(arg), Map.class);
                    if (MapUtils.isNotEmpty(map)) {
                        Arrays.stream(MASK_PARAMS).forEach(map::remove);
                    }
                    stringJoiner.add(JSON.toJSONString(map));
                }
            }
            log.setParam(stringJoiner.toString());
            // 设置操作方法名称
            String clazz = joinPoint.getTarget().getClass().getName();
            String method = joinPoint.getSignature().getName();
            log.setMethod(clazz + "." + method);
            // 设置操作描述
            log.setDescription(controllerLog.description());
            // 设置操作类型
            log.setOperator(controllerLog.operator().getOperatorValue());
            // 设置操作结果
            log.setResult(1);
            // 设置响应内容
            Map throwableMap = JSON.parseObject(JSON.toJSONString(e), Map.class);
            Object returnCodeString = throwableMap.get("returnCode");
            if (Objects.nonNull(returnCodeString)) {
                HashMap jsonMap = new HashMap<>();
                jsonMap.put("name", throwableMap.get("returnCode"));
                jsonMap.put("msg", throwableMap.get("msg"));
                log.setJson(JSON.toJSONString(jsonMap));
            } else {
                log.setJson(e.getMessage());
            }
            // 设置操作用户ID
            Long userId = null;
            try {
                userId = LoginUtils.getLoginUserId();
            } catch (Exception ignored) {
            }
            log.setUserId(Objects.isNull(userId) ? USER_ID_THREAD_LOCAL.get() : userId);
            // 设置接口URI和请求方法类型
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            log.setUri(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
            // 设置操作用户的IP和地址
            log.setIp(NetUtils.getIpAddressByRequest(request));
            log.setLocation(NetUtils.getRegionByRequest(request));
            // 设置接口访问耗时
            StopWatch stopWatch = COST_TIME_THREAD_LOCAL.get();
            stopWatch.stop();
            long time = stopWatch.getTime();
            log.setTime(time);
            // 插入数据库
            LOG_MAPPER.insert(log);
        } catch (Exception exception) {
            log.error("日志记录报错：{}", exception.getMessage());
            exception.printStackTrace();
        } finally {
            USER_ID_THREAD_LOCAL.remove();
            COST_TIME_THREAD_LOCAL.remove();
        }
    }

    /**
     * 过滤掉不可用JSON表达的参数和一些Http访问参数
     *
     * @param o 校验过滤对象
     */
    @SuppressWarnings("rawtypes")
    public boolean isFilterObject(Object o) {
        Class<?> clazz = o.getClass();
        if (clazz.isArray()) {
            return clazz.getComponentType().isAssignableFrom(MultipartFile.class);
        } else if (Collection.class.isAssignableFrom(clazz)) {
            Collection collection = (Collection) o;
            for (Object value : collection) {
                return value instanceof MultipartFile;
            }
        } else if (Map.class.isAssignableFrom(clazz)) {
            Map map = (Map) o;
            for (Object value : map.values()) {
                return value instanceof MultipartFile;
            }
        }
        return o instanceof MultipartFile || o instanceof HttpServletRequest || o instanceof HttpServletResponse || o instanceof BindingResult;
    }

}
