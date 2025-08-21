package top.sharehome.springbootinittemplate.common.base;

import java.util.regex.Pattern;

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
    String REGEX_NUMBER_AND_LETTER_STR = "^[0-9a-zA-Z]+$";
    Pattern REGEX_NUMBER_AND_LETTER_PATTERN = Pattern.compile(REGEX_NUMBER_AND_LETTER_STR);

    /**
     * 只包含汉字的正则表达式
     */
    String REGEX_CHINESE_CHARACTER_STR = "^[\\u4e00-\\u9fa5]+$";
    Pattern REGEX_CHINESE_CHARACTER_PATTERN = Pattern.compile(REGEX_CHINESE_CHARACTER_STR);

    /**
     * 只包含数字、英文和汉字的正则表达式
     */
    String REGEX_NUMBER_AND_LETTER_AND_CHINESE_CHARACTER_STR = "^[0-9a-zA-Z\\u4e00-\\u9fa5]+$";
    Pattern REGEX_NUMBER_AND_LETTER_AND_CHINESE_CHARACTER_PATTERN = Pattern.compile(REGEX_NUMBER_AND_LETTER_AND_CHINESE_CHARACTER_STR);

    /**
     * 中文名字的正则表达式
     */
    String REGEX_CHINESE_NAME_STR = "^[\\u4e00-\\u9fa5·]{2,16}$";
    Pattern REGEX_CHINESE_NAME_PATTERN = Pattern.compile(REGEX_CHINESE_NAME_STR);

    /**
     * 身份证的正则表达式
     */
    String REGEX_ID_CARD_STR = "^[1-9]\\d{5}((((19|[2-9][0-9])\\d{2})(0[13578]|1[02])(0[1-9]|[12][0-9]|3[01]))|(((19|[2-9][0-9])\\d{2})(0[13456789]|1[012])(0[1-9]|[12][0-9]|30))|(((19|[2-9][0-9])\\d{2})02(0[1-9]|1[0-9]|2[0-8]))|(((1[6-9]|[2-9][0-9])(0[48]|[2468][048]|[13579][26])|((16|[2468][048]|[3579][26])00))0229))\\d{3}[0-9Xx]$";
    Pattern REGEX_ID_CARD_PATTERN = Pattern.compile(REGEX_ID_CARD_STR);

    /**
     * 中国手机号的正则表达式
     */
    String REGEX_CHINESE_PHONE_STR = "^1[3-9](\\d{9})$";
    Pattern REGEX_CHINESE_PHONE_PATTERN = Pattern.compile(REGEX_CHINESE_PHONE_STR);

    /**
     * 邮箱的正则表达式
     */
    String REGEX_EMAIL_STR = "^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)+$";
    Pattern REGEX_EMAIL_PATTERN = Pattern.compile(REGEX_EMAIL_STR);

    /**
     * 车牌的正则表达式
     */
    String REGEX_LICENSE_PLATE_STR = "^[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领][A-HJ-NP-Z][A-HJ-NP-Z0-9]{4}[A-HJ-NP-Z0-9挂学警港澳]$";
    Pattern REGEX_LICENSE_PLATE_PATTERN = Pattern.compile(REGEX_LICENSE_PLATE_STR);

    /**
     * 银行卡的正则表达式
     */
    String REGEX_BANK_CARD_STR = "^([1-9])(\\d{15}|\\d{18})$";
    Pattern REGEX_BANK_CARD_PATTERN = Pattern.compile(REGEX_BANK_CARD_STR);

    /**
     * IPv4地址的正则表达式
     */
    String REGEX_IPV_4_STR = "^(?!0)(?!.*\\.\\.)(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])(\\.(25[0-5]|2[0-4][0-9]|1[0-9]{2}|[1-9]?[0-9])){3}$";
    Pattern REGEX_IPV_4_PATTERN = Pattern.compile(REGEX_IPV_4_STR);

    /**
     * IPv6地址的正则表达式
     */
    String REGEX_IPV_6_STR = "^(([0-9a-fA-F]{1,4}:){7}([0-9a-fA-F]{1,4}|:)|([0-9a-fA-F]{1,4}:){1,7}:|([0-9a-fA-F]{1,4}:){1,6}:[0-9a-fA-F]{1,4}|([0-9a-fA-F]{1,4}:){1,5}(:[0-9a-fA-F]{1,4}){1,2}|([0-9a-fA-F]{1,4}:){1,4}(:[0-9a-fA-F]{1,4}){1,3}|([0-9a-fA-F]{1,4}:){1,3}(:[0-9a-fA-F]{1,4}){1,4}|([0-9a-fA-F]{1,4}:){1,2}(:[0-9a-fA-F]{1,4}){1,5}|[0-9a-fA-F]{1,4}:((:[0-9a-fA-F]{1,4}){1,6})|:((:[0-9a-fA-F]{1,4}){1,7}|:)|fe80:(:[0-9a-fA-F]{0,4}){0,4}%[0-9a-zA-Z]+|::(ffff(:0{1,4})?:)?((25[0-5]|(2[0-4]|1?[0-9]|)[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9]|)[0-9])|([0-9a-fA-F]{1,4}:){1,4}:((25[0-5]|(2[0-4]|1?[0-9]|)[0-9])\\.){3}(25[0-5]|(2[0-4]|1?[0-9]|)[0-9]))$";
    Pattern REGEX_IPV_6_PATTERN = Pattern.compile(REGEX_IPV_6_STR);

    /**
     * MAC地址的正则表达式
     */
    String REGEX_MAC_STR = "^([0-9A-Fa-f]{2}(:[0-9A-Fa-f]{2}){5}|[0-9A-Fa-f]{2}(-[0-9A-Fa-f]{2}){5})$";
    Pattern REGEX_MAC_PATTERN = Pattern.compile(REGEX_MAC_STR);

    /**
     * 未知文件类型后缀
     */
    String UNKNOWN_FILE_TYPE_SUFFIX = "unknown";

    /**
     * 未知文件ContentType
     */
    String UNKNOWN_FILE_CONTENT_TYPE = "application/octet-stream";

    /**
     * 数据库不允许为NULL时，默认的ID
     */
    Long NULL_ID = -1L;

}