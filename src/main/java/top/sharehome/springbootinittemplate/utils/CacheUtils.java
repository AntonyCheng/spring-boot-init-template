package top.sharehome.springbootinittemplate.utils;

import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
    @Resource
    private RedissonClient redissonClient;

    private static final String CACHE_KEY_PREFIX = "CACHE_";

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @param <T>
     */
    public <T> void put(String key, T value) {
        RBucket<T> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value);
    }

    /**
     * 设置缓存，同时设置过期时间
     *
     * @param key
     * @param value
     * @param expired
     * @param <T>
     */
    public <T> void put(String key, T value, long expired) {
        RBucket<T> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 获取缓存
     *
     * @param key
     * @param <T>
     * @return
     */
    public <T> T get(String key) {
        RBucket<T> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + key);
        return bucket.get();
    }

    private static final String STRING_PREFIX = "STRING_";

    /**
     * 设置String类型缓存
     *
     * @param key
     * @param value
     * @return
     */
    public void putString(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value);
    }

    /**
     * 设置String类型缓存，同时设置过期时间
     *
     * @param key
     * @param value
     * @param expired
     */
    public void putString(String key, String value, long expired) {
        RBucket<String> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置String类型缓存
     *
     * @param key
     * @param value
     */
    public void putStringIfExists(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key
     * @param value
     * @param expired
     */
    public void putStringIfExists(String key, String value, long expired) {
        RBucket<String> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfExists(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果不存在则设置String类型缓存
     *
     * @param key
     * @param value
     */
    public void putStringIfAbsent(String key, String value) {
        RBucket<String> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key
     * @param value
     * @param expired
     */
    public void putStringIfAbsent(String key, String value, long expired) {
        RBucket<String> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfAbsent(value, Duration.ofSeconds(expired));
    }

    /**
     * 获取String类型缓存
     *
     * @param key
     * @return
     */
    public String getString(String key) {
        RBucket<String> bucket = redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.get();
    }

    /**
     * 判断String类型缓存是否存在
     *
     * @param key
     * @return
     */
    public boolean existsString(String key) {
        return redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key).isExists();
    }

    /**
     * 删除String类型缓存
     *
     * @param key
     */
    public void removeString(String key) {
        redissonClient.getBucket(CACHE_KEY_PREFIX + STRING_PREFIX + key).delete();
    }

    private static final String LIST_PREFIX = "LIST_";

    /**
     * 设置List类型缓存
     *
     * @param key
     * @param valueList
     */
    public <T> void putList(String key, List<T> valueList) {
        RList<T> list = redissonClient.getList(CACHE_KEY_PREFIX + LIST_PREFIX + key);
        list.addAll(valueList);
    }

    /**
     * 设置List类型缓存，同时设置过期时间
     *
     * @param key
     * @param valueList
     */
    public <T> void putList(String key, List<T> valueList, long expired) {
        RList<T> list = redissonClient.getList(CACHE_KEY_PREFIX + LIST_PREFIX + key);
        list.addAll(valueList);
        list.expire(Duration.ofSeconds(expired));
    }

    /**
     * 获取List类型缓存
     *
     * @param key
     * @return
     */
    public <T> List<T> getList(String key) {
        return redissonClient.getList(CACHE_KEY_PREFIX + LIST_PREFIX + key);
    }

    /**
     * 判断List类型缓存是否存在
     *
     * @param key
     * @return
     */
    public boolean existsList(String key) {
        return redissonClient.getBucket(CACHE_KEY_PREFIX + LIST_PREFIX + key).isExists();
    }

    /**
     * 删除List类型缓存
     *
     * @param key
     */
    public void removeList(String key) {
        redissonClient.getBucket(CACHE_KEY_PREFIX + LIST_PREFIX + key).delete();
    }

    private static final String SET_PREFIX = "SET_";

    /**
     * 设置Set类型缓存
     *
     * @param key
     * @param valueSet
     */
    public <T> void putSet(String key, Set<T> valueSet) {
        RSet<T> set = redissonClient.getSet(CACHE_KEY_PREFIX + SET_PREFIX + key);
        set.addAll(valueSet);
    }

    /**
     * 设置Set类型缓存，同时设置过期时间
     *
     * @param key
     * @param valueSet
     */
    public <T> void putSet(String key, Set<T> valueSet, long expired) {
        RSet<T> set = redissonClient.getSet(CACHE_KEY_PREFIX + SET_PREFIX + key);
        set.addAll(valueSet);
        set.expire(Duration.ofSeconds(expired));
    }

    /**
     * 获取Set类型缓存
     *
     * @param key
     * @return
     */
    public <T> Set<T> getSet(String key) {
        return redissonClient.getSet(CACHE_KEY_PREFIX + SET_PREFIX + key);
    }

    /**
     * 判断Set类型缓存是否存在
     *
     * @param key
     * @return
     */
    public boolean existsSet(String key) {
        return redissonClient.getBucket(CACHE_KEY_PREFIX + SET_PREFIX + key).isExists();
    }

    /**
     * 删除Set类型缓存
     *
     * @param key
     */
    public void removeSet(String key) {
        redissonClient.getBucket(CACHE_KEY_PREFIX + SET_PREFIX + key).delete();
    }

    private static final String MAP_PREFIX = "MAP_";

    /**
     * 设置Map类型缓存
     *
     * @param key
     * @param valueMap
     */
    public <K, V> void putMap(String key, Map<K, V> valueMap) {
        RMap<K, V> map = redissonClient.getMap(CACHE_KEY_PREFIX + MAP_PREFIX + key);
        map.putAll(valueMap);
    }

    /**
     * 设置Map类型缓存，同时设置过期时间
     *
     * @param key
     * @param valueMap
     */
    public <K, V> void putMap(String key, Map<K, V> valueMap, long expired) {
        RMap<K, V> map = redissonClient.getMap(CACHE_KEY_PREFIX + MAP_PREFIX + key);
        map.putAll(valueMap);
        map.expire(Duration.ofSeconds(expired));
    }

    /**
     * 获取Map类型缓存
     *
     * @param key
     * @return
     */
    public <K, V> Map<K, V> getMap(String key) {
        RMap<K, V> map = redissonClient.getMap(CACHE_KEY_PREFIX + MAP_PREFIX + key);
        return new HashMap<K, V>(map);
    }

    /**
     * 判断Map类型缓存是否存在
     *
     * @param key
     * @return
     */
    public boolean existsMap(String key) {
        return redissonClient.getBucket(CACHE_KEY_PREFIX + MAP_PREFIX + key).isExists();
    }

    /**
     * 删除Map类型缓存
     *
     * @param key
     */
    public void removeMap(String key) {
        redissonClient.getBucket(CACHE_KEY_PREFIX + MAP_PREFIX + key).delete();
    }
}
