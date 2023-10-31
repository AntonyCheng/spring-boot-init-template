package top.sharehome.springbootinittemplate.common.base;

/**
 * todo：这里主要记录开发者个人的返回码枚举值，模板中扮演的角色是HttpStatus的补充，主要搭配自定义异常和统一返回类一起使用
 * 返回码规范如下：
 * 200------操作成功
 * 500------操作失败
 * 600------系统警告
 * 1XXXX----后端逻辑错误
 * 2XXXX----数据库错误
 * 3XXXX----网络组件错误
 * 4XXXX----其他错误
 *
 * @author AntonyCheng
 */

public enum ReturnCode {

    /**
     * 操作成功 200
     */
    SUCCESS(HttpStatus.SUCCESS, "操作成功"),

    /**
     * 操作失败 500
     */
    FAIL(HttpStatus.ERROR, "操作失败"),

    /**
     * 系统警告 600
     */
    WARN(HttpStatus.WARN, "系统警告"),

    /**
     * 账户名称校验失败 11000
     */
    USERNAME_VALIDATION_FAILED(11000, "账户名称校验失败"),

    /**
     * 账户名称已经存在 11001
     */
    USERNAME_ALREADY_EXISTS(11001, "账户名称已经存在"),

    /**
     * 账户名称包含特殊字符 11002
     */
    PASSWORD_AND_SECONDARY_PASSWORD_NOT_SAME(11002, "密码和二次密码不相同"),

    /**
     * 密码校验失败 11003
     */
    PASSWORD_VERIFICATION_FAILED(11003, "密码校验失败"),

    /**
     * 用户基本信息校验失败 11004
     */
    USER_BASIC_INFORMATION_VERIFICATION_FAILED(11004, "用户基本信息校验失败"),

    /**
     * 用户账户不存在 11005
     */
    USER_ACCOUNT_DOES_NOT_EXIST(11005, "用户账户不存在"),

    /**
     * 用户账户被封禁 11006
     */
    USER_ACCOUNT_BANNED(11006, "用户账户被封禁"),

    /**
     * 用户账号或密码错误 11007
     */
    WRONG_USER_ACCOUNT_OR_PASSWORD(11007, "用户账号或密码错误"),

    /**
     * 用户登录已过期 11008
     */
    USER_LOGIN_HAS_EXPIRED(11008, "用户登录已过期"),

    /**
     * 用户操作异常 11009
     */
    ABNORMAL_USER_OPERATION(11009, "用户操作异常"),

    /**
     * 用户设备异常 11010
     */
    ABNORMAL_USER_EQUIPMENT(11010, "用户设备异常"),

    /**
     * 手机格式校验失败 12000
     */
    PHONE_FORMAT_VERIFICATION_FAILED(12000, "手机格式校验失败"),

    /**
     * 邮箱格式校验失败 12001
     */
    EMAIL_FORMAT_VERIFICATION_FAILED(12001, "邮箱格式校验失败"),

    /**
     * 访问未授权 13000
     */
    ACCESS_UNAUTHORIZED(13000, "访问未授权"),

    /**
     * 请求必填参数为空 13001
     */
    REQUEST_REQUIRED_PARAMETER_IS_EMPTY(13001, "请求必填参数为空"),

    /**
     * 参数格式不匹配 13002
     */
    PARAMETER_FORMAT_MISMATCH(13002, "参数格式不匹配"),

    /**
     * 用户请求次数太多 13003
     */
    TOO_MANY_REQUESTS(13003, "用户请求次数太多"),

    /**
     * 用户上传文件异常 14000
     */
    FILE_UPLOAD_EXCEPTION(14000, "用户上传文件异常"),

    /**
     * 用户上传文件类型不匹配 14001
     */
    USER_UPLOADED_FILE_TYPE_MISMATCH(14001, "用户上传文件类型不匹配"),

    /**
     * 用户上传文件太大 14002
     */
    USER_UPLOADED_FILE_IS_TOO_LARGE(14002, "用户上传文件太大"),

    /**
     * 用户上传图片太大 14003
     */
    USER_UPLOADED_IMAGE_IS_TOO_LARGE(14003, "用户上传图片太大"),

    /**
     * 用户上传视频太大 14004
     */
    USER_UPLOADED_VIDEO_IS_TOO_LARGE(14004, "用户上传视频太大"),

    /**
     * 用户上传压缩文件太大 14005
     */
    USER_UPLOADED_ZIP_IS_TOO_LARGE(14005, "用户上传压缩文件太大"),

    /**
     * 数据库服务出错 20000
     */
    ERRORS_OCCURRED_IN_THE_DATABASE_SERVICE(20000, "数据库服务出错"),

    /**
     * 消息中间件服务出错 30000
     */
    MQ_SERVICE_ERROR(30000, "消息中间件服务出错"),

    /**
     * 内存数据库服务出错 30001
     */
    MAIN_MEMORY_DATABASE_SERVICE_ERROR(30001, "内存数据库服务出错"),

    /**
     * 搜索引擎服务出错 30002
     */
    SEARCH_ENGINE_SERVICE_ERROR(30002, "搜索引擎服务出错"),

    /**
     * 网关服务出错 30003
     */
    GATEWAY_SERVICE_ERROR(30003, "网关服务出错");

    final private int code;

    final private String msg;

    ReturnCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
