package top.sharehome.springbootinittemplate.common.base;

/**
 * 通用常量信息
 *
 * @author AntonyCheng
 */
public interface Constants {

    /**
     * UTF-8 字符集
     */
    String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    String GBK = "GBK";

    /**
     * www主域
     */
    String WWW = "www.";

    /**
     * http请求
     */
    String HTTP = "http://";

    /**
     * https请求
     */
    String HTTPS = "https://";

    /**
     * 成功标记
     */
    Integer SUCCESS = HttpStatus.SUCCESS;

    /**
     * 失败标记
     */
    Integer FAIL = HttpStatus.ERROR;

    /**
     * 用户启用状态
     */
    Integer USER_ENABLE_STATE = 0;

    /**
     * 用户禁用状态
     */
    Integer USER_DISABLE_STATE = 1;

    /**
     * 登录成功
     */
    String LOGIN_SUCCESS = "LoginSuccess";

    /**
     * 注销
     */
    String LOGOUT = "Logout";

    /**
     * 注册
     */
    String REGISTER = "Register";

    /**
     * 登录失败
     */
    String LOGIN_FAIL = "LoginFail";

    /**
     * 验证码有效期（分钟）
     */
    long CAPTCHA_EXPIRATION = 2;

    /**
     * 存放在缓存中的用户键
     */
    String LOGIN_USER_KEY = "loginUser";

    /**
     * 用户角色admin
     */
    String ROLE_ADMIN = "admin";

    /**
     * 用户角色user
     */
    String ROLE_USER = "user";

    /**
     * 只包含数字和英文的正则表达式
     */
    String REGEX_NUMBER_AND_LETTER = "^[0-9a-zA-Z]+$";

    /**
     * 邮箱的正则表达式
     */
    String REGEX_MAIL = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";

    /**
     * 未知文件类型后缀
     */
    String UNKNOWN_FILE_TYPE_SUFFIX = "unknown";

    /**
     * 未知文件ContentType
     */
    String UNKNOWN_FILE_CONTENT_TYPE = "application/octet-stream";

}