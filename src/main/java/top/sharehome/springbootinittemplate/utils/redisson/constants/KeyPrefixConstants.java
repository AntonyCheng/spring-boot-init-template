package top.sharehome.springbootinittemplate.utils.redisson.constants;

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
     * 限流Key前缀
     */
    String RATE_LIMIT_PREFIX = "RATE_";

    /**
     * 分布式锁Key前缀
     */
    String LOCK_PREFIX = "LOCK_";

    /**
     * 验证码Key前缀
     */
    String CAPTCHA_PREFIX = "CAPTCHA_";

}