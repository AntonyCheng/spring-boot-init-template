package top.sharehome.springbootinittemplate.utils.redisson.cache;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 缓存工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class CacheUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    /**
     * 设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param <T>   泛型T
     */
    public static <T> void put(String key, T value) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.set(value);
    }

    /**
     * 不需要默认前缀，设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param <T>   泛型T
     */
    public static <T> void putNoPrefix(String key, T value) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
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
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 不需要默认前缀，设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     * @param <T>     泛型T
     */
    public static <T> void putNoPrefix(String key, T value, long expired) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 设置空缓存
     *
     * @param key 缓存键
     */
    public static void putNull(String key) {
        RBucket<?> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.set(null);
    }

    /**
     * 设置空缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param expired 过期时间
     */
    public static void putNull(String key, long expired) {
        RBucket<?> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.set(null, Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> void putIfExists(String key, T value) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> void putIfExists(String key, T value, long expired) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.setIfExists(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果不存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> void putIfAbsent(String key, T value) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> void putIfAbsent(String key, T value, long expired) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.setIfAbsent(value, Duration.ofSeconds(expired));
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> T get(String key) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        return bucket.get();
    }

    /**
     * 不需要默认前缀，获取缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> T getNoPrefix(String key) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get();
    }

    /**
     * 根据类型获取缓存
     *
     * @param key  缓存键
     * @param type 返回类型
     * @param <T>  泛型T
     * @param <R>  泛型R
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R get(String key, Class<R> type) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        return (R) bucket.get();
    }

    /**
     * 不需要默认前缀，根据类型获取缓存
     *
     * @param key  缓存键
     * @param type 返回类型
     * @param <T>  泛型T
     * @param <R>  泛型R
     */
    @SuppressWarnings("unchecked")
    public static <T, R> R getNoPrefix(String key, Class<R> type) {
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        return (R) bucket.get();
    }

    /**
     * 使用通配符模糊获取缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getKeysByPattern(String keyPattern) {
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<String>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_PREFIX, ""));
        });
        return res;
    }

    /**
     * 使用通配符模糊获取缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getKeyValuesByPattern(String keyPattern) {
        return getKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", get(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (T) map.get("value")));
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getExpired(String key) {
        long expireTime = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 不需要默认前缀，获取缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getExpiredNoPrefix(String key) {
        long expireTime = REDISSON_CLIENT.getBucket(key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 判断缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean exists(String key) {
        return REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsNoPrefix(String key) {
        return REDISSON_CLIENT.getBucket(key).isExists();
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public static void delete(String key) {
        REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除缓存
     *
     * @param key 缓存键
     */
    public static void deleteNoPrefix(String key) {
        REDISSON_CLIENT.getBucket(key).delete();
    }

    /**
     * 使用通配符模糊删除缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteByPattern(String keyPattern) {
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_PREFIX + keyPattern);
    }

    /**
     * 设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putString(String key, CharSequence value) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value);
    }

    /**
     * 不需要默认前缀，设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putStringNoPrefix(String key, CharSequence value) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value);
    }

    /**
     * 设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putString(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 不需要默认前缀，设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putStringNoPrefix(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putStringIfExists(String key, CharSequence value) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putStringIfExists(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfExists(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果不存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putStringIfAbsent(String key, CharSequence value) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putStringIfAbsent(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.setIfAbsent(value, Duration.ofSeconds(expired));
    }

    /**
     * 获取String类型缓存
     *
     * @param key 缓存键
     */
    public static String getString(String key) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.get();
    }

    /**
     * 不需要默认前缀，获取String类型缓存
     *
     * @param key 缓存键
     */
    public static String getStringNoPrefix(String key) {
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get();
    }

    /**
     * 使用通配符模糊获取String类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getStringKeysByPattern(String keyPattern) {
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_STRING_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<String>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_STRING_PREFIX, ""));
        });
        return res;
    }

    /**
     * 使用通配符模糊获取String类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, String> getStringKeyValuesByPattern(String keyPattern) {
        return getStringKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getString(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (String) map.get("value")));
    }

    /**
     * 获取String类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getStringExpired(String key) {
        long expireTime = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 不需要默认前缀，获取String类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getStringExpiredNoPrefix(String key) {
        long expireTime = REDISSON_CLIENT.getBucket(key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 判断String类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsString(String key) {
        return REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断String类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsStringNoPrefix(String key) {
        return REDISSON_CLIENT.getBucket(key).isExists();
    }

    /**
     * 删除String类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteString(String key) {
        REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除String类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteStringNoPrefix(String key) {
        REDISSON_CLIENT.getBucket(key).delete();
    }

    /**
     * 使用通配符模糊删除String类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteStringByPattern(String keyPattern) {
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_STRING_PREFIX + keyPattern);
    }

    /**
     * 设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putNumber(String key, Number value) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        bucket.set(value);
    }

    /**
     * 不需要默认前缀，设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putNumberNoPrefix(String key, Number value) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        bucket.set(value);
    }

    /**
     * 设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putNumber(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 不需要默认前缀，设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putNumberNoPrefix(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key);
        bucket.set(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putNumberIfExists(String key, CharSequence value) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putNumberIfExists(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        bucket.setIfExists(value, Duration.ofSeconds(expired));
    }

    /**
     * 如果不存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putNumberIfAbsent(String key, CharSequence value) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putNumberIfAbsent(String key, CharSequence value, long expired) {
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        bucket.setIfAbsent(value, Duration.ofSeconds(expired));
    }

    /**
     * 获取Number类型缓存
     *
     * @param key 缓存键
     */
    public static Number getNumber(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get();
    }

    /**
     * 不需要默认前缀，获取Number类型缓存
     *
     * @param key 缓存键
     */
    public static Number getNumberNoPrefix(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get();
    }

    /**
     * 获取Number中byte类型缓存
     *
     * @param key 缓存键
     */
    public static byte getNumberByteValue(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().byteValue();
    }

    /**
     * 获取Number中int类型缓存
     *
     * @param key 缓存键
     */
    public static int getNumberIntValue(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().intValue();
    }

    /**
     * 获取Number中short类型缓存
     *
     * @param key 缓存键
     */
    public static int getNumberShortValue(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().shortValue();
    }

    /**
     * 获取Number中long类型缓存
     *
     * @param key 缓存键
     */
    public static long getNumberLongValue(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().longValue();
    }

    /**
     * 获取Number中float类型缓存
     *
     * @param key 缓存键
     */
    public static float getNumberFloatValue(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().floatValue();
    }

    /**
     * 获取Number中double类型缓存
     *
     * @param key 缓存键
     */
    public static double getNumberDoubleValue(String key) {
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().doubleValue();
    }

    /**
     * 使用通配符模糊获取Number类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getNumberKeysByPattern(String keyPattern) {
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_NUMBER_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<String>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_NUMBER_PREFIX, ""));
        });
        return res;
    }

    /**
     * 使用通配符模糊获取缓存Number类型键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, Number> getNumberKeyValuesByPattern(String keyPattern) {
        return getNumberKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getNumber(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Number) map.get("value")));
    }

    /**
     * 获取Number类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getNumberExpired(String key) {
        long expireTime = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 不需要默认前缀，获取Number类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getNumberExpiredNoPrefix(String key) {
        long expireTime = REDISSON_CLIENT.getBucket(key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 判断Number类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsNumber(String key) {
        return REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断Number类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsNumberNoPrefix(String key) {
        return REDISSON_CLIENT.getBucket(key).isExists();
    }

    /**
     * 删除Number类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteNumber(String key) {
        REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除Number类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteNumberNoPrefix(String key) {
        REDISSON_CLIENT.getBucket(key).delete();
    }

    /**
     * 使用通配符模糊删除Number类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteNumberByPattern(String keyPattern) {
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_NUMBER_PREFIX + keyPattern);
    }

    /**
     * 设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putList(String key, List<T> valueList) {
        RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
        list.addAll(valueList);
    }

    /**
     * 不需要默认前缀，设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putListNoPrefix(String key, List<T> valueList) {
        RList<T> list = REDISSON_CLIENT.getList(key);
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
        RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
        list.addAll(valueList);
        list.expire(Duration.ofSeconds(expired));
    }

    /**
     * 不需要默认前缀，设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putListNoPrefix(String key, List<T> valueList, long expired) {
        RList<T> list = REDISSON_CLIENT.getList(key);
        list.addAll(valueList);
        list.expire(Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putListIfExists(String key, List<T> valueList) {
        if (existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            list.addAll(valueList);
        }
    }

    /**
     * 如果存在则设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    public static <T> void putListIfExists(String key, List<T> valueList, long expired) {
        if (existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            list.addAll(valueList);
            list.expire(Duration.ofSeconds(expired));
        }
    }

    /**
     * 如果不存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putListIfAbsent(String key, List<T> valueList) {
        if (!existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            list.addAll(valueList);
        }
    }

    /**
     * 如果不存在则设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    public static <T> void putListIfAbsent(String key, List<T> valueList, long expired) {
        if (!existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            list.addAll(valueList);
            list.expire(Duration.ofSeconds(expired));
        }
    }

    /**
     * 获取List类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> List<T> getList(String key) {
        return REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
    }

    /**
     * 不需要默认前缀，获取List类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> List<T> getListNoPrefix(String key) {
        return REDISSON_CLIENT.getList(key);
    }

    /**
     * 使用通配符模糊获取List类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getListKeysByPattern(String keyPattern) {
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_LIST_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<String>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_LIST_PREFIX, ""));
        });
        return res;
    }

    /**
     * 使用通配符模糊获取缓List类型存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static Map<String, List<Object>> getListKeyValuesByPattern(String keyPattern) {
        return getListKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getList(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (List<Object>) map.get("value")));
    }

    /**
     * 获取List类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getListExpired(String key) {
        long expireTime = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 不需要默认前缀，获取List类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getListExpiredNoPrefix(String key) {
        long expireTime = REDISSON_CLIENT.getList(key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 判断List类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsList(String key) {
        return REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断List类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsListNoPrefix(String key) {
        return REDISSON_CLIENT.getList(key).isExists();
    }

    /**
     * 删除List类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteList(String key) {
        REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除List类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteListNoPrefix(String key) {
        REDISSON_CLIENT.getList(key).delete();
    }

    /**
     * 使用通配符模糊删除List类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteListByPattern(String keyPattern) {
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_LIST_PREFIX + keyPattern);
    }

    /**
     * 设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSet(String key, Set<T> valueSet) {
        RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
        set.addAll(valueSet);
    }

    /**
     * 不需要默认前缀，设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSetNoPrefix(String key, Set<T> valueSet) {
        RSet<T> set = REDISSON_CLIENT.getSet(key);
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
        RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
        set.addAll(valueSet);
        set.expire(Duration.ofSeconds(expired));
    }

    /**
     * 不需要默认前缀，设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSetNoPrefix(String key, Set<T> valueSet, long expired) {
        RSet<T> set = REDISSON_CLIENT.getSet(key);
        set.addAll(valueSet);
        set.expire(Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSetIfExists(String key, Set<T> valueSet) {
        if (existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            set.addAll(valueSet);
        }
    }

    /**
     * 如果存在则设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param expired  过期时间
     * @param <T>      泛型T
     */
    public static <T> void putSetIfExists(String key, Set<T> valueSet, long expired) {
        if (existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            set.addAll(valueSet);
            set.expire(Duration.ofSeconds(expired));
        }
    }

    /**
     * 如果不存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSetIfAbsent(String key, Set<T> valueSet) {
        if (!existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            set.addAll(valueSet);
        }
    }

    /**
     * 如果不存在则设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param expired  过期时间
     * @param <T>      泛型T
     */
    public static <T> void putSetIfAbsent(String key, Set<T> valueSet, long expired) {
        if (!existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            set.addAll(valueSet);
            set.expire(Duration.ofSeconds(expired));
        }
    }

    /**
     * 获取Set类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> Set<T> getSet(String key) {
        return REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
    }

    /**
     * 不需要默认前缀，获取Set类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> Set<T> getSetNoPrefix(String key) {
        return REDISSON_CLIENT.getSet(key);
    }

    /**
     * 使用通配符模糊获取Set类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getSetKeysByPattern(String keyPattern) {
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_SET_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<String>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_SET_PREFIX, ""));
        });
        return res;
    }

    /**
     * 使用通配符模糊获取Set类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Set<Object>> getSetKeyValuesByPattern(String keyPattern) {
        return getSetKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getSet(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Set<Object>) map.get("value")));
    }

    /**
     * 获取Set类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getSetExpired(String key) {
        long expireTime = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 不需要默认前缀，获取Set类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getSetExpiredNoPrefix(String key) {
        long expireTime = REDISSON_CLIENT.getSet(key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 判断Set类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsSet(String key) {
        return REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断Set类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsSetNoPrefix(String key) {
        return REDISSON_CLIENT.getSet(key).isExists();
    }

    /**
     * 删除Set类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteSet(String key) {
        REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除Set类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteSetNoPrefix(String key) {
        REDISSON_CLIENT.getSet(key).delete();
    }

    /**
     * 使用通配符模糊删除Set类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteSetByPattern(String keyPattern) {
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_SET_PREFIX + keyPattern);
    }

    /**
     * 设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMap(String key, Map<K, V> valueMap) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
        map.putAll(valueMap);
    }

    /**
     * 不需要默认前缀，设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMapNoPrefix(String key, Map<K, V> valueMap) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(key);
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
        RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
        map.putAll(valueMap);
        map.expire(Duration.ofSeconds(expired));
    }

    /**
     * 不需要默认前缀，设置Map类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMapNoPrefix(String key, Map<K, V> valueMap, long expired) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(key);
        map.putAll(valueMap);
        map.expire(Duration.ofSeconds(expired));
    }

    /**
     * 如果存在则设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMapIfExists(String key, Map<K, V> valueMap) {
        if (existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
        }
    }

    /**
     * 如果存在则设置Map类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param expired  过期时间
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMapIfExists(String key, Map<K, V> valueMap, long expired) {
        if (existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
            map.expire(Duration.ofSeconds(expired));
        }
    }

    /**
     * 如果不存在则设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMapIfAbsent(String key, Map<K, V> valueMap) {
        if (!existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
        }
    }

    /**
     * 如果不存在则设置Map类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param expired  过期时间
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMapIfAbsent(String key, Map<K, V> valueMap, long expired) {
        if (!existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
            map.expire(Duration.ofSeconds(expired));
        }
    }

    /**
     * 获取Map类型缓存
     *
     * @param key 缓存键
     * @param <K> 泛型K
     * @param <V> 泛型V
     */
    public static <K, V> Map<K, V> getMap(String key) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
        return new HashMap<K, V>(map);
    }

    /**
     * 不需要默认前缀，获取Map类型缓存
     *
     * @param key 缓存键
     * @param <K> 泛型K
     * @param <V> 泛型V
     */
    public static <K, V> Map<K, V> getMapNoPrefix(String key) {
        RMap<K, V> map = REDISSON_CLIENT.getMap(key);
        return new HashMap<K, V>(map);
    }

    /**
     * 使用通配符模糊获取Map类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getMapKeysByPattern(String keyPattern) {
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_MAP_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<String>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_MAP_PREFIX, ""));
        });
        return res;
    }

    /**
     * 使用通配符模糊获取Map类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Map<Object, Object>> getMapKeyValuesByPattern(String keyPattern) {
        return getMapKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getMap(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Map<Object, Object>) map.get("value")));
    }

    /**
     * 获取Map类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getMapExpired(String key) {
        long expireTime = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 不需要默认前缀，获取Map类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getMapExpiredNoPrefix(String key) {
        long expireTime = REDISSON_CLIENT.getMap(key).getExpireTime();
        if (Objects.equals(expireTime, -2L)) {
            return 0L;
        }
        return (expireTime - System.currentTimeMillis()) / 1000;
    }

    /**
     * 判断Map类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsMap(String key) {
        return REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断Map类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsMapNoPrefix(String key) {
        return REDISSON_CLIENT.getMap(key).isExists();
    }

    /**
     * 删除Map类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteMap(String key) {
        REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除Map类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteMapNoPrefix(String key) {
        REDISSON_CLIENT.getMap(key).delete();
    }

    /**
     * 使用通配符模糊删除Map类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteMapByPattern(String keyPattern) {
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_MAP_PREFIX + keyPattern);
    }

}