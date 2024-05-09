package top.sharehome.springbootinittemplate.utils.caffeine;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;

import java.util.Map;

/**
 * 本地缓存工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class LocalCacheUtils {

    /**
     * 被封装的caffeine缓存操作类
     */
    @SuppressWarnings("unchecked")
    private static final Cache<String, Object> CAFFEINE_CLIENT = SpringContextHolder.getBean("localCache", Cache.class);

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void put(String key, Object value) {
        CAFFEINE_CLIENT.put(key, value);
    }

    /**
     * 设置批量缓存
     *
     * @param keyAndValues 缓存键值对
     */
    public static void putAll(Map<String, Object> keyAndValues) {
        CAFFEINE_CLIENT.putAll(keyAndValues);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @return 返回结果
     */
    public static Object get(String key) {
        return CAFFEINE_CLIENT.getIfPresent(key);
    }

    /**
     * 获取批量缓存
     *
     * @param keys 缓存键集合
     * @return 返回结果
     */
    public static Map<String, Object> getAll(Iterable<String> keys) {
        return CAFFEINE_CLIENT.getAllPresent(keys);
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public static void delete(String key) {
        CAFFEINE_CLIENT.invalidate(key);
    }

    /**
     * 删除批量缓存
     *
     * @param keys 缓存键集合
     */
    public static void delete(Iterable<String> keys) {
        CAFFEINE_CLIENT.invalidateAll(keys);
    }

    /**
     * 清空缓存
     */
    public static void clear() {
        CAFFEINE_CLIENT.invalidateAll();
    }

}