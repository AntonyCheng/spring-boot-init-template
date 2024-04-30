package top.sharehome.springbootinittemplate.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import cn.dev33.satoken.exception.SameTokenInvalidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.util.NestedServletException;
import top.sharehome.springbootinittemplate.common.base.HttpStatus;
import top.sharehome.springbootinittemplate.common.base.R;
import top.sharehome.springbootinittemplate.exception.customize.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import java.util.Objects;

/**
 * Security全局异常处理器
 *
 * @author AntonyCheng
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    // todo 第一个todo中的异常均为系统自带异常，抛出的错误码被包含在HttpStatus接口中

    /**
     * 用户权限异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(NotPermissionException.class)
    public R<Void> handleNotPermissionException(NotPermissionException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',权限码校验失败'{}'", requestUri, e.getMessage());
        return R.fail(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 角色角色异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(NotRoleException.class)
    public R<Void> handleNotRoleException(NotRoleException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',角色权限校验失败'{}'", requestUri, e.getMessage());
        return R.fail(HttpStatus.FORBIDDEN, "没有访问权限，请联系管理员授权");
    }

    /**
     * 认证失败
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(NotLoginException.class)
    public R<Void> handleNotLoginException(NotLoginException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',认证失败'{}',无法访问系统资源", requestUri, e.getMessage());
        return R.fail(HttpStatus.UNAUTHORIZED, "认证失败，无法访问系统资源");
    }

    /**
     * 无效认证
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(SameTokenInvalidException.class)
    public R<Void> handleSameTokenInvalidException(SameTokenInvalidException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',内网认证失败'{}',无法访问系统资源", requestUri, e.getMessage());
        return R.fail(HttpStatus.UNAUTHORIZED, "认证失败，无法访问系统资源");
    }

    /**
     * 请求方式不支持
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public R<Void> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',不支持'{}'请求", requestUri, e.getMethod());
        return R.fail(HttpStatus.BAD_METHOD, e.getMessage());
    }

    /**
     * 请求路径中缺少必需的路径变量
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(MissingPathVariableException.class)
    public R<Void> handleMissingPathVariableException(MissingPathVariableException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求路径中缺少必需的路径变量'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, String.format("请求路径中缺少必需的路径变量[%s]", e.getVariableName()));
    }

    /**
     * 请求参数类型不匹配
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public R<Void> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, String.format("请求参数类型不匹配，参数[%s]要求类型为：'%s'，但输入值为：'%s'", e.getName(), Objects.isNull(e.getRequiredType()) ? "None" : e.getRequiredType().getName(), e.getValue()));
    }

    /**
     * 文件分段参数接收时异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(MultipartException.class)
    public R<Void> handleMultipartException(MultipartException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求参数类型不匹配'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.BAD_REQUEST, "文件参数异常");
    }

    /**
     * 拦截未知的运行时异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(RuntimeException.class)
    public R<Void> handleRuntimeException(RuntimeException e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生未知异常.", requestUri, e);
        return R.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * 系统异常
     *
     * @param e       异常
     * @param request 请求
     * @return 返回结果
     */
    @ExceptionHandler(Exception.class)
    public R<Void> handleException(Exception e, HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        log.error("请求地址'{}',发生系统异常.", requestUri, e);
        return R.fail(HttpStatus.ERROR, e.getMessage());
    }

    /**
     * Validation校验注解校验异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(ValidationException.class)
    public R<Void> handleValidationException(ValidationException e) {
        log.error(e.getMessage(), e);
        return R.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * Validation校验注解校验异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public R<Void> handleConstraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return R.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * 路径不存在异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public R<Void> handleNoHandlerFoundException(NoHandlerFoundException e) {
        log.error(e.getMessage(), e);
        return R.fail(HttpStatus.NOT_FOUND, e.getMessage());
    }

    /**
     * 数据重复异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public R<Void> handleDuplicateKeyException(DuplicateKeyException e) {
        log.error(e.getMessage(), e);
        return R.fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    /**
     * 方法参数校验异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        String message = Objects.isNull(e.getBindingResult().getFieldError()) ? "No Message" : e.getBindingResult().getFieldError().getDefaultMessage();
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 自定义验证异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(BindException.class)
    public R<Void> handleBindException(BindException e) {
        log.error(e.getMessage(), e);
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return R.fail(HttpStatus.BAD_REQUEST, message);
    }

    /**
     * 自定义未找到Bean异常
     * todo Beta测试版本，为合理适配前端框架上传文件时找不到上传文件Bean时报错，一般是因为开发者没有开启OSS功能，这个不影响业务功能，一旦有合理方案就会做出修改
     */
    @ExceptionHandler(NestedServletException.class)
    public R<Void> handleNestedServletException(NestedServletException e) {
        log.error(e.getMessage(), e);
        return R.fail(HttpStatus.ERROR, "功能服务未开启");
    }

    // todo 第一个todo中的异常均为自定义异常，抛出的错误码被包含在ReturnCode枚举类中

    /**
     * 自定义返回异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeReturnException.class)
    public R<String> handleCustomizeReturnException(CustomizeReturnException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }

    /**
     * 自定义文件异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeFileException.class)
    public R<String> handleCustomizeFileException(CustomizeFileException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }

    /**
     * 自定义锁异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeLockException.class)
    public R<String> handleCustomizeLockException(CustomizeLockException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }

    /**
     * 自定义邮件异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeMailException.class)
    public R<String> handleCustomizeMailException(CustomizeMailException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }

    /**
     * 自定义Excel异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeExcelException.class)
    public R<String> handleCustomizeExcelException(CustomizeExcelException e) {
        log.error(e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg(), e);
        int code = e.getReturnCode().getCode();
        String msg = e.getMsg() == null ? e.getReturnCode().getMsg() : e.getMsg();
        return R.fail(code, msg);
    }

    /**
     * 自定义数据库事务异常
     *
     * @param e 异常
     * @return 返回结果
     */
    @ExceptionHandler(CustomizeTransactionException.class)
    public R<String> handleCustomizeTransactionException(CustomizeTransactionException e) {
        log.error(e.getMessage(), e);
        return R.fail(e.getReturnCode());
    }

}