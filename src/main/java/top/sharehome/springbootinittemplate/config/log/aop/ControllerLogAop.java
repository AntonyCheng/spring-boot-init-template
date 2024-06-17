package top.sharehome.springbootinittemplate.config.log.aop;

import com.alibaba.fastjson.JSON;
import com.alibaba.ttl.TransmittableThreadLocal;
import org.apache.commons.lang3.time.StopWatch;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.log.annotation.ControllerLog;
import top.sharehome.springbootinittemplate.mapper.LogMapper;
import top.sharehome.springbootinittemplate.model.entity.Log;
import top.sharehome.springbootinittemplate.utils.net.NetUtils;
import top.sharehome.springbootinittemplate.utils.satoken.LoginUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * 日志的切面类
 *
 * @author AntonyCheng
 */
@Component
@EnableAspectJAutoProxy
@Aspect
public class ControllerLogAop {

    /**
     * 获取LogMapper
     */
    private static final LogMapper LOG_MAPPER = SpringContextHolder.getBean(LogMapper.class);

    /**
     * 系统默认屏蔽的请求/响应字段
     * User：password、checkPassword、oldPassword、newPassword、checkNewPassword
     */
    private static final String[] MASK_PARAMS = {"password", "checkPassword", "oldPassword", "newPassword", "newPassword", "checkNewPassword"};

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
    public void doAfterReturning(JoinPoint joinPoint, ControllerLog controllerLog, R returnResult) {
        try {
            Log log = new Log();
            // todo：做请求内容屏蔽
            Object[] args = joinPoint.getArgs();
            String clazz = joinPoint.getTarget().getClass().getName();
            String method = joinPoint.getSignature().getName();
            log.setMethod(clazz + "." + method);
            log.setDescription(controllerLog.description());
            log.setOperator(controllerLog.operator().getOperatorValue());
            log.setResult(returnResult.getCode() == 200 ? 0 : 1);
            // todo：做响应内容屏蔽
            log.setJson(JSON.toJSONString(returnResult));
            try {
                log.setUserId(LoginUtils.getLoginUserId());
            } catch (Exception e) {
                log.setUserId(null);
            }
            ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = servletRequestAttributes.getRequest();
            log.setUri(request.getRequestURI());
            log.setRequestMethod(request.getMethod());
            log.setIp(NetUtils.getIpAddressByRequest(request));
            log.setLocation(NetUtils.getRegionByRequest(request));
            StopWatch stopWatch = COST_TIME_THREAD_LOCAL.get();
            stopWatch.stop();
            long time = stopWatch.getTime();
            log.setTime(time);
            LOG_MAPPER.insert(log);
        } finally {
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
    public void doAfterThrowing(JoinPoint joinPoint, ControllerLog controllerLog, Throwable e) {

    }

    // todo：考虑将异常通知和后置通知放在同一个方法内
    private void doHandle(JoinPoint joinPoint, ControllerLog controllerLog, R returnResult, Throwable e) {

    }

}
