package top.sharehome.springbootinittemplate.utils.redisson;

/**
 * 缓存Key前缀常量
 *
 * @author AntonyCheng
 */
public interface KeyPrefixConstants {

    /**
     * 不带有类型的缓存Key前缀
     */
    String CACHE_KEY_PREFIX = "CACHE_";

    /**
     * String类型的缓存Key前准
     */
    String STRING_PREFIX = CACHE_KEY_PREFIX + "STRING_";

    /**
     * Number类型的缓存Key前准
     */
    String NUMBER_PREFIX = CACHE_KEY_PREFIX + "NUMBER_";

    /**
     * List类型的缓存Key前缀
     */
    String LIST_PREFIX = CACHE_KEY_PREFIX + "LIST_";

    /**
     * Set类型的缓存Key前缀
     */
    String SET_PREFIX = CACHE_KEY_PREFIX + "SET_";

    /**
     * Map类型的缓存Key前缀
     */
    String MAP_PREFIX = CACHE_KEY_PREFIX + "MAP_";

    /**
     * 系统限流Key前缀
     */
    String SYSTEM_RATE_LIMIT_PREFIX = "SYSTEM_RATE_";

    /**
     * 自定义限流Key前缀
     */
    String CUSTOMIZE_RATE_LIMIT_PREFIX = "CUSTOMIZE_RATE_";

    /**
     * 分布式锁Key前缀
     */
    String LOCK_PREFIX = "LOCK_";

    /**
     * 验证码Key前缀
     */
    String CAPTCHA_PREFIX = "CAPTCHA_";

    /**
     * 邮箱Key前缀
     */
    String EMAIL_PREFIX = "EMAIL_";

}