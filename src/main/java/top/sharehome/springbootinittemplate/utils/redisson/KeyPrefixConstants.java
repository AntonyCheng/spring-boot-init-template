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
    String CACHE_PREFIX = "CACHE:";

    /**
     * String类型的缓存Key前准
     */
    String CACHE_STRING_PREFIX = CACHE_PREFIX + "STRING:";

    /**
     * Number类型的缓存Key前准
     */
    String CACHE_NUMBER_PREFIX = CACHE_PREFIX + "NUMBER:";

    /**
     * List类型的缓存Key前缀
     */
    String CACHE_LIST_PREFIX = CACHE_PREFIX + "LIST:";

    /**
     * Set类型的缓存Key前缀
     */
    String CACHE_SET_PREFIX = CACHE_PREFIX + "SET:";

    /**
     * Map类型的缓存Key前缀
     */
    String CACHE_MAP_PREFIX = CACHE_PREFIX + "MAP:";

    // todo 幂等相关键前缀

    /**
     * 幂等Key前缀
     */
    String IDEMPOTENT_PREFIX = "IDEMPOTENT:";

    // todo 防重相关键前缀

    /**
     * 防重Key前缀
     */
    String PREVENT_REPEAT = "PREVENT_REPEAT:";

    // todo 限流相关键前缀

    /**
     * 限流Key前缀
     */
    String RATE_LIMIT_PREFIX = "RATE_LIMIT:";

    /**
     * 工具类限流Key前缀
     */
    String RATE_LIMIT_UTILS_PREFIX = RATE_LIMIT_PREFIX + "UTILS:";

    /**
     * 注解限流Key前缀
     */
    String RATE_LIMIT_ANNOTATION_PREFIX = RATE_LIMIT_PREFIX + "ANNOTATION:";

    // todo 分布式锁相关键前缀

    /**
     * 分布式锁Key前缀
     */
    String LOCK_PREFIX = "LOCK:";

    // todo 验证码相关键前缀

    /**
     * 验证码Key前缀
     */
    String CAPTCHA_PREFIX = "CAPTCHA:";

    // todo 邮箱相关键前缀

    /**
     * 邮箱Key前缀
     */
    String EMAIL_PREFIX = "EMAIL:";

    /**
     * 找回密码邮箱Key前缀
     */
    String EMAIL_RETRIEVE_PASSWORD_PREFIX = EMAIL_PREFIX + "RETRIEVE_PASSWORD:";

    /**
     * 注册激活邮箱Key前缀
     */
    String EMAIL_REGISTER_ACTIVATE_PREFIX = EMAIL_PREFIX + "REGISTER_ACTIVATE:";

}