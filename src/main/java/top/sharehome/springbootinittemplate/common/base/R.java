package top.sharehome.springbootinittemplate.common.base;

import cn.dev33.satoken.stp.StpUtil;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 响应信息主体
 *
 * @author AntonyCheng
 */
@Data
@NoArgsConstructor
public class R<T> implements Serializable {

    /**
     * 成功状态码
     */
    public static final int SUCCESS = ReturnCode.SUCCESS.getCode();

    /**
     * 成功响应值
     */
    public static final String SUCCESS_MSG = ReturnCode.SUCCESS.getMsg();

    /**
     * 失败状态码
     */
    public static final int FAIL = ReturnCode.FAIL.getCode();

    /**
     * 失败响应值
     */
    public static final String FAIL_MSG = ReturnCode.FAIL.getMsg();

    /**
     * 警告状态码
     */
    public static final int WARN = ReturnCode.WARN.getCode();

    /**
     * 警告响应值
     */
    public static final String WARN_MSG = ReturnCode.WARN.getMsg();

    /**
     * 序列化UID
     */
    private static final long serialVersionUID = 1L;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String msg;

    /**
     * 数据对象
     */
    private T data;

    private static <T> R<T> restResult(T data, int code, String msg) {
        R<T> r = new R<>();
        r.setCode(code);
        r.setData(data);
        r.setMsg(msg);
        return r;
    }

    /**
     * 是否失败
     */
    public static <T> Boolean isError(R<T> ret) {
        return FAIL == ret.getCode();
    }

    /**
     * 是否成功
     */
    public static <T> Boolean isSuccess(R<T> ret) {
        return SUCCESS == ret.getCode();
    }

    // todo 成功响应方法
    public static <T> R<T> ok() {
        return restResult(null, SUCCESS, SUCCESS_MSG);
    }

    public static R<Map<String, Object>> okWithToken() {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("token", StpUtil.getTokenValue());
        res.put("res", null);
        return restResult(res, SUCCESS, SUCCESS_MSG);
    }

    public static <T> R<T> ok(T data) {
        return restResult(data, SUCCESS, SUCCESS_MSG);
    }

    public static <T> R<Map<String, Object>> okWithToken(T data) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("token", StpUtil.getTokenValue());
        res.put("res", data);
        return restResult(res, SUCCESS, SUCCESS_MSG);
    }

    public static <T> R<T> ok(String msg) {
        return restResult(null, SUCCESS, msg);
    }

    public static <T> R<Map<String, Object>> okWithToken(String msg) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("token", StpUtil.getTokenValue());
        res.put("res", null);
        return restResult(res, SUCCESS, msg);
    }

    public static <T> R<T> ok(String msg, T data) {
        return restResult(data, SUCCESS, msg);
    }

    public static <T> R<Map<String, Object>> okWithToken(String msg, T data) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("token", StpUtil.getTokenValue());
        res.put("res", data);
        return restResult(res, SUCCESS, msg);
    }

    // todo 失败响应方法
    public static <T> R<T> fail() {
        return restResult(null, FAIL, FAIL_MSG);
    }

    public static <T> R<T> fail(String msg) {
        return restResult(null, FAIL, msg);
    }

    public static <T> R<T> fail(T data) {
        return restResult(data, FAIL, FAIL_MSG);
    }

    public static <T> R<T> fail(String msg, T data) {
        return restResult(data, FAIL, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return restResult(null, code, msg);
    }

    public static <T> R<T> fail(ReturnCode returnCode) {
        return restResult(null, returnCode.getCode(), returnCode.getMsg());
    }

    public static <T> R<T> fail(T data, ReturnCode returnCode) {
        return restResult(data, returnCode.getCode(), returnCode.getMsg());
    }

    // todo 警告响应方法
    public static <T> R<T> warn() {
        return restResult(null, WARN, WARN_MSG);
    }

    public static <T> R<T> warn(String msg) {
        return restResult(null, WARN, msg);
    }

    public static <T> R<T> warn(String msg, T data) {
        return restResult(data, WARN, msg);
    }

    // todo 空响应方法
    public static <T> R<T> empty() {
        return null;
    }

}