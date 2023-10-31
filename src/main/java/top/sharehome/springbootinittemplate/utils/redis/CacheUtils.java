package top.sharehome.springbootinittemplate.utils.redis;

import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 缓存工具类
 *
 * @author AntonyCheng
 * @since 2023/10/30 20:04:36
 */
@Component
public class CacheUtils {
    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    private static final String CACHE_KEY_PREFIX = "CACHE_";

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param <T>   泛型T
     */
    public static <T> void put(String key, T value) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value);
    }

    /**
     * 设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     * @param <T>     泛型T
     */
    public static <T> void put(String key, T value, long expired) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     * @return 返回结果
     */
    public static <T> T get(String key) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + key);
        return bucket.get();
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     * @return 返回结果
     */
    public static boolean exists(String key) {
        return REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + key).isExists();
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public static void remove(String key) {
        REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + key).delete();
    }

    private static final String STRING_PREFIX = "STRING_";

    /**
     * 设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putString(String key, String value) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value);
    }

    /**
     * 设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putString(String key, String value, long expired) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putStringIfExists(String key, String value) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putStringIfExists(String key, String value, long expired) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfExists(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果不存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putStringIfAbsent(String key, String value) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putStringIfAbsent(String key, String value, long expired) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfAbsent(value, Duration.ofSeconds(expired));
    }

    /**
     * 获取String类型缓存
     *
     * @param key 缓存键
     * @return 返回结果
     */
    public static String getString(String key) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.get();
    }

    /**
     * 判断String类型缓存是否存在
     *
     * @param key 缓存键
     * @return 返回结果
     */
    public static boolean existsString(String key) {
        return REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key).isExists();
    }

    /**
     * 删除String类型缓存
     *
     * @param key 缓存键
     */
    public static void removeString(String key) {
        REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key).delete();
    }

    private static final String LIST_PREFIX = "LIST_";

    /**
     * 设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putList(String key, List<T> valueList) {
        RList<T> list = REDISSON_CLIENT.getList(CACHE_KEY_PREFIX + LIST_PREFIX + key);
        list.addAll(valueList);
    }

    /**
     * 设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putList(String key, List<T> valueList, long expired) {
        RList<T> list = REDISSON_CLIENT.getList(CACHE_KEY_PREFIX + LIST_PREFIX + key);
        list.addAll(valueList);
        list.expire(Duration.ofSeconds(expired));
    }

    /**
     * 获取List类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     * @return 返回结果
     */
    public static <T> List<T> getList(String key) {
        return REDISSON_CLIENT.getList(CACHE_KEY_PREFIX + LIST_PREFIX + key);
    }

    /**
     * 判断List类型缓存是否存在
     *
     * @param key 缓存键
     * @return 返回结果
     */
    public static boolean existsList(String key) {
        return REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + LIST_PREFIX + key).isExists();
    }

    /**
     * 删除List类型缓存
     *
     * @param key 缓存键
     */
    public static void removeList(String key) {
        REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + LIST_PREFIX + key).delete();
    }

    private static final String SET_PREFIX = "SET_";

    /**
     * 设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSet(String key, Set<T> valueSet) {
        RSet<T> set = REDISSON_CLIENT.getSet(CACHE_KEY_PREFIX + SET_PREFIX + key);
        set.addAll(valueSet);
    }

    /**
     * 设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSet(String key, Set<T> valueSet, long expired) {
        RSet<T> set = REDISSON_CLIENT.getSet(CACHE_KEY_PREFIX + SET_PREFIX + key);
        set.addAll(valueSet);
        set.expire(Duration.ofSeconds(expired));
    }

    /**
     * 获取Set类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     * @return 返回结果
     */
    public static <T> Set<T> getSet(String key) {
        return REDISSON_CLIENT.getSet(CACHE_KEY_PREFIX + SET_PREFIX + key);
    }

    /**
     * 判断Set类型缓存是否存在
     *
     * @param key 缓存键
     * @return 返回结果
     */
    public static boolean existsSet(String key) {
        return REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + SET_PREFIX + key).isExists();
    }

    /**
     * 删除Set类型缓存
     *
     * @param key 缓存键
     */
    public static void removeSet(String key) {
        REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + SET_PREFIX + key).delete();
    }

    private static final String MAP_PREFIX = "MAP_";

    /**
     * 设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMap(String key, Map<K, V> valueMap) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(CACHE_KEY_PREFIX + MAP_PREFIX + key);
        map.putAll(valueMap);
    }

    /**
     * 设置Map类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMap(String key, Map<K, V> valueMap, long expired) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(CACHE_KEY_PREFIX + MAP_PREFIX + key);
        map.putAll(valueMap);
        map.expire(Duration.ofSeconds(expired));
    }

    /**
     * 获取Map类型缓存
     *
     * @param key 缓存键
     * @param <K> 泛型K
     * @param <V> 泛型V
     * @return 返回结果
     */
    public static <K, V> Map<K, V> getMap(String key) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(CACHE_KEY_PREFIX + MAP_PREFIX + key);
        return new HashMap<K, V>(map);
    }

    /**
     * 判断Map类型缓存是否存在
     *
     * @param key 缓存键
     * @return 返回结果
     */
    public static boolean existsMap(String key) {
        return REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + MAP_PREFIX + key).isExists();
    }

    /**
     * 删除Map类型缓存
     *
     * @param key 缓存键
     */
    public static void removeMap(String key) {
        REDISSON_CLIENT.getBucket(CACHE_KEY_PREFIX + MAP_PREFIX + key).delete();
    }
}
