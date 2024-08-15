package top.sharehome.springbootinittemplate.utils.redisson;

/**
 * 缓存Key前缀常量
 *
 * @author AntonyCheng
 */
public interface KeyPrefixConstants {

    // todo 缓存相关键前缀

    /**
     * 缓存Key前缀
     */
    String CACHE_PREFIX = "CACHE_";

    /**
     * String类型的缓存Key前准
     */
    String CACHE_STRING_PREFIX = CACHE_PREFIX + "STRING_";

    /**
     * Number类型的缓存Key前准
     */
    String CACHE_NUMBER_PREFIX = CACHE_PREFIX + "NUMBER_";

    /**
     * List类型的缓存Key前缀
     */
    String CACHE_LIST_PREFIX = CACHE_PREFIX + "LIST_";

    /**
     * Set类型的缓存Key前缀
     */
    String CACHE_SET_PREFIX = CACHE_PREFIX + "SET_";

    /**
     * Map类型的缓存Key前缀
     */
    String CACHE_MAP_PREFIX = CACHE_PREFIX + "MAP_";

    // todo 幂等相关键前缀

    /**
     * 幂等Key前缀
     */
    String IDEMPOTENT_PREFIX = "IDEMPOTENT_";

    // todo 防重相关键前缀

    /**
     * 防重Key前缀
     */
    String PREVENT_REPEAT = "PREVENT_REPEAT_";

    // todo 限流相关键前缀

    /**
     * 限流Key前缀
     */
    String RATE_LIMIT_PREFIX = "RATE_LIMIT_";

    /**
     * 系统限流Key前缀
     */
    String RATE_LIMIT_SYSTEM_PREFIX = RATE_LIMIT_PREFIX + "SYSTEM_";

    /**
     * 自定义限流Key前缀
     */
    String RATE_LIMIT_CUSTOMIZE_PREFIX = RATE_LIMIT_PREFIX + "CUSTOMIZE_";

    // todo 分布式锁相关键前缀

    /**
     * 分布式锁Key前缀
     */
    String LOCK_PREFIX = "LOCK_";

    // todo 验证码相关键前缀

    /**
     * 验证码Key前缀
     */
    String CAPTCHA_PREFIX = "CAPTCHA_";

    // todo 邮箱相关键前缀

    /**
     * 邮箱Key前缀
     */
    String EMAIL_PREFIX = "EMAIL_";

    /**
     * 找回密码邮箱Key前缀
     */
    String EMAIL_RETRIEVE_PASSWORD_PREFIX = EMAIL_PREFIX + "RETRIEVE_PASSWORD_";

    /**
     * 注册激活邮箱Key前缀
     */
    String EMAIL_REGISTER_ACTIVATE_PREFIX = EMAIL_PREFIX + "REGISTER_ACTIVATE_";

}