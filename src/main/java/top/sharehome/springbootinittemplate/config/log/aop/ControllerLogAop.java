package top.sharehome.springbootinittemplate.config.log.aop;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.alibaba.ttl.TransmittableThreadLocal;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.exception.CustomizeException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.mapper.LogMapper;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.concurrent.TimeUnit;

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
    private static final String[] MASK_PARAMS = {"password", "checkPassword", "oldPassword", "newPassword", "newPassword", "checkNewPassword", "captcha", "token", "email", "passwordCode"};

    /**
     * 记录日志操作用户ID，TransmittableThreadLocal是Alibaba继承ThreadLocal的一个类，它适用于适用于复杂的线程池、异步任务等场景，由于该日志记录过程中可能会存在这些场景，所以能够保证线程本地变量的传递性。
     */
    private static final ThreadLocal<Long> USER_ID_THREAD_LOCAL = new TransmittableThreadLocal<>();

    /**
     * 计算日志操作访问耗时
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
     * @param returnResult  响应结果
     */
    @AfterReturning(value = "pointCutMethod()&&@annotation(controllerLog)", returning = "returnResult")
    @SuppressWarnings({"CallToPrintStackTrace", "rawtypes"})
    public void doAfterReturning(JoinPoint joinPoint, ControllerLog controllerLog, Object returnResult) {
        try {
            Log log = new Log();
            // 设置操作方法名称
            String clazz = joinPoint.getTarget().getClass().getName();
            String method = joinPoint.getSignature().getName();
            log.setMethod(clazz + "." + method);
            // 设置操作描述
            log.setDescription(controllerLog.description());
            // 设置操作类型
            log.setOperator(controllerLog.operator().getOperatorValue());
            // 设置操作结果
            log.setResult(Objects.isNull(returnResult) || (returnResult instanceof R<?> r && r.getCode() == R.SUCCESS) ? 0 : 1);
            // 设置响应内容
            TypeReference<HashMap<String, Object>> type = new TypeReference<>() {
            };
            HashMap<String, Object> resMap = JSON.parseObject(JSON.toJSONString(returnResult), type);
            if (Objects.isNull(resMap)) {
                log.setJson("{}");
            } else {
                Object data = resMap.get("data");
                if (Objects.nonNull(data) && data instanceof Map dataMap) {
                    Arrays.stream(MASK_PARAMS).forEach(dataMap::remove);
                    Arrays.stream(controllerLog.maskParams()).forEach(dataMap::remove);
                    resMap.put("data", dataMap);
                    String json = JSON.toJSONString(resMap);
                    if (json.length() > 2000) {
                        json = StringUtils.substring(json, 0, 2000) + "...";
                    }
                    log.setJson(json);
                } else {
                    String json = JSON.toJSONString(resMap);
                    if (json.length() > 2000) {
                        json = StringUtils.substring(json, 0, 2000) + "...";
                    }
                    log.setJson(json);
                }
            }
            // 设置操作用户ID
            Long userId = null;
            try {
                userId = LoginUtils.getLoginUserId();
            } catch (Exception ignored) {
            }
            log.setUserId(Objects.isNull(userId) ? USER_ID_THREAD_LOCAL.get() : userId);
            // 设置接口URI
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(servletRequestAttributes)) {
                throw new CustomizeReturnException(ReturnCode.USER_SENT_INVALID_REQUEST, "无法获取请求");
            }
            HttpServletRequest request = servletRequestAttributes.getRequest();
            log.setUri(request.getRequestURI());
            // 请求方法类型
            String requestMethod = request.getMethod();
            log.setRequestMethod(requestMethod);
            // 设置操作参数
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
                        HashMap<String, Object> map = JSON.parseObject(JSON.toJSONString(arg), type);
                        if (MapUtils.isNotEmpty(map)) {
                            Arrays.stream(MASK_PARAMS).forEach(map::remove);
                            Arrays.stream(controllerLog.maskParams()).forEach(map::remove);
                        }
                        stringJoiner.add(JSON.toJSONString(map));
                    }
                }
                param = stringJoiner.toString();
            } else {
                Arrays.stream(MASK_PARAMS).forEach(parameterMap::remove);
                Arrays.stream(controllerLog.maskParams()).forEach(parameterMap::remove);
                param = JSON.toJSONString(parameterMap);
            }
            if (param.length() > 2000) {
                param = StringUtils.substring(param, 0, 2000) + "...";
            }
            log.setParam(StringUtils.isBlank(param) ? "{}" : param);
            // 设置操作用户IP
            log.setIp(NetUtils.getIpByRequest(request));
            // 设置操作用户地址
            log.setLocation(NetUtils.getRegionByRequest(request));
            // 设置接口访问耗时
            StopWatch stopWatch = COST_TIME_THREAD_LOCAL.get();
            stopWatch.stop();
            long time = stopWatch.getDuration().toMillis();
            log.setTime(time);
            // 插入数据库
            LOG_MAPPER.insert(log);
        } catch (Exception exception) {
            log.error("记录正常日志记录报错：{}", exception.getMessage());
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
    @SuppressWarnings({"CallToPrintStackTrace"})
    public void doAfterThrowing(JoinPoint joinPoint, ControllerLog controllerLog, final Throwable e) {
        try {
            Log log = new Log();
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
            HashMap<String, Object> jsonMap = new HashMap<>();
            if (e instanceof CustomizeException customizeException) {
                jsonMap.put("code", customizeException.getReturnCode().getCode());
                jsonMap.put("name", customizeException.getReturnCode().name());
                jsonMap.put("msg", customizeException.getReturnCode().getMsg() + " ==> [" + customizeException.getMsg() + "]");
            } else {
                StackTraceElement[] stackTrace = e.getStackTrace();
                if (stackTrace.length != 0) {
                    jsonMap.put("class", stackTrace[0].getClassName());
                    jsonMap.put("method", stackTrace[0].getMethodName());
                    jsonMap.put("line", stackTrace[0].getLineNumber());
                    jsonMap.put("exception", e.toString());
                }
            }
            String json = JSON.toJSONString(jsonMap);
            if (json.length() > 2000 && json.charAt(2000) != '}') {
                json = json.substring(0, 2000) + "...}";
            }
            log.setJson(json);
            // 设置操作用户ID
            Long userId = null;
            try {
                userId = LoginUtils.getLoginUserId();
            } catch (Exception ignored) {
            }
            log.setUserId(Objects.isNull(userId) ? USER_ID_THREAD_LOCAL.get() : userId);
            // 设置接口URI
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (Objects.isNull(servletRequestAttributes)) {
                throw new CustomizeReturnException(ReturnCode.USER_SENT_INVALID_REQUEST, "无法获取请求");
            }
            HttpServletRequest request = servletRequestAttributes.getRequest();
            log.setUri(request.getRequestURI());
            // 请求方法类型
            String requestMethod = request.getMethod();
            log.setRequestMethod(requestMethod);
            // 设置操作参数
            Map<String, String[]> parameter = Collections.unmodifiableMap(request.getParameterMap());
            Map<String, String> parameterMap = new HashMap<>();
            for (Map.Entry<String, String[]> entry : parameter.entrySet()) {
                parameterMap.put(entry.getKey(), StringUtils.join(entry.getValue(), ","));
            }
            String param = null;
            if (MapUtils.isEmpty(parameterMap) && (HttpMethod.POST.name().equals(requestMethod) || HttpMethod.PUT.name().equals(requestMethod))) {
                StringJoiner stringJoiner = new StringJoiner(",");
                Object[] args = joinPoint.getArgs();
                for (Object arg : args) {
                    if (Objects.nonNull(arg) && !isFilterObject(arg)) {
                        TypeReference<HashMap<String, Object>> type = new TypeReference<>() {
                        };
                        HashMap<String, Object> map = JSON.parseObject(JSON.toJSONString(arg), type);
                        if (MapUtils.isNotEmpty(map)) {
                            Arrays.stream(MASK_PARAMS).forEach(map::remove);
                            Arrays.stream(controllerLog.maskParams()).forEach(map::remove);
                        }
                        stringJoiner.add(JSON.toJSONString(map));
                    }
                }
                param = stringJoiner.toString();
            } else {
                Arrays.stream(MASK_PARAMS).forEach(parameterMap::remove);
                Arrays.stream(controllerLog.maskParams()).forEach(parameterMap::remove);
                param = JSON.toJSONString(parameterMap);
            }
            if (param.length() > 2000 && param.charAt(2000) != '}') {
                param = StringUtils.substring(param, 0, 2000) + "...}";
            }
            log.setParam(StringUtils.isBlank(param) ? "{}" : param);
            // 设置操作用户IP
            log.setIp(NetUtils.getIpByRequest(request));
            // 设置操作用户地址
            log.setLocation(NetUtils.getRegionByRequest(request));
            // 设置接口访问耗时
            StopWatch stopWatch = COST_TIME_THREAD_LOCAL.get();
            stopWatch.stop();
            long time = stopWatch.getTime(TimeUnit.NANOSECONDS);
            log.setTime(time);
            // 插入数据库
            LOG_MAPPER.insert(log);
        } catch (Exception exception) {
            log.error("记录异常日志记录报错：{}", exception.getMessage());
            exception.printStackTrace();
        } finally {
            USER_ID_THREAD_LOCAL.remove();
            COST_TIME_THREAD_LOCAL.remove();
        }
    }

    /**
     * 过滤掉不可用JSON表达的参数和一些Http访问参数
     *
     * @param obj 校验过滤对象
     */
    @SuppressWarnings({"rawtypes", "BooleanMethodIsAlwaysInverted"})
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
        return obj instanceof MultipartFile
                || obj instanceof HttpServletRequest
                || obj instanceof HttpServletResponse
                || obj instanceof BindingResult;
    }

}
