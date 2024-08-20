package top.sharehome.springbootinittemplate.utils.redisson.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.*;
import org.redisson.client.codec.StringCodec;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeRedissonException;
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
@SuppressWarnings("unused")
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
        verifyParameters(key, value);
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
        verifyParameters(key, value);
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
    public static <T> void put(String key, T value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        bucket.set(value, expired);
    }

    /**
     * 不需要默认前缀，设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     * @param <T>     泛型T
     */
    public static <T> void putNoPrefix(String key, T value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        bucket.set(value, expired);
    }

    /**
     * 如果存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putIfExists(String key, T value) {
        verifyParameters(key, value);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        return bucket.setIfExists(value);
    }

    /**
     * 不需要默认前缀，如果存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putNoPrefixIfExists(String key, T value) {
        verifyParameters(key, value);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putIfExists(String key, T value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        return bucket.setIfExists(value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putNoPrefixIfExists(String key, T value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfExists(value, expired);
    }

    /**
     * 如果不存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putIfAbsent(String key, T value) {
        verifyParameters(key, value);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        return bucket.setIfAbsent(value);
    }

    /**
     * 不需要默认前缀，如果不存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putNoPrefixIfAbsent(String key, T value) {
        verifyParameters(key, value);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putIfAbsent(String key, T value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key);
        return bucket.setIfAbsent(value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putNoPrefixIfAbsent(String key, T value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfAbsent(value, expired);
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> T get(String key) {
        verifyParameters(key);
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
        verifyParameters(key);
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
        verifyParameters(key, type);
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
        verifyParameters(key, type);
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
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_PREFIX, ""));
        });
        return res;
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getKeysNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(res::add);
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
        verifyParameters(keyPattern);
        return getKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", get(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (T) map.get("value")));
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static <T> Map<String, T> getKeyValuesNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        return getKeysNoPrefixByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getNoPrefix(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (T) map.get("value")));
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getExpired(String key) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsNoPrefix(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(key).isExists();
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public static void delete(String key) {
        verifyParameters(key);
        REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除缓存
     *
     * @param key 缓存键
     */
    public static void deleteNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_PREFIX + keyPattern);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(keyPattern);
    }

    /**
     * 设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putString(String key, CharSequence value) {
        verifyParameters(key, value);
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
        verifyParameters(key, value);
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
    public static void putString(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        bucket.set(value, expired);
    }

    /**
     * 不需要默认前缀，设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putStringNoPrefix(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key, StringCodec.INSTANCE);
        bucket.set(value, expired);
    }

    /**
     * 如果存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringIfExists(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.setIfExists(value);
    }

    /**
     * 不需要默认前缀，如果存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringNoPrefixIfExists(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key, StringCodec.INSTANCE);
        return bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringIfExists(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.setIfExists(value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringNoPrefixIfExists(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key, StringCodec.INSTANCE);
        return bucket.setIfExists(value, expired);
    }

    /**
     * 如果不存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringIfAbsent(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.setIfAbsent(value);
    }

    /**
     * 不需要默认前缀，如果不存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringNoPrefixIfAbsent(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key, StringCodec.INSTANCE);
        return bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringIfAbsent(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.setIfAbsent(value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringNoPrefixIfAbsent(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key, StringCodec.INSTANCE);
        return bucket.setIfAbsent(value, expired);
    }

    /**
     * 获取String类型缓存
     *
     * @param key 缓存键
     */
    public static String getString(String key) {
        verifyParameters(key);
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key, StringCodec.INSTANCE);
        return bucket.get();
    }

    /**
     * 不需要默认前缀，获取String类型缓存
     *
     * @param key 缓存键
     */
    public static String getStringNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_STRING_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_STRING_PREFIX, ""));
        });
        return res;
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取String类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getStringKeysNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(res::add);
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
        verifyParameters(keyPattern);
        return getStringKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getString(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (String) map.get("value")));
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取String类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, String> getStringKeyValuesNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        return getStringKeysNoPrefixByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getStringNoPrefix(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (String) map.get("value")));
    }

    /**
     * 获取String类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getStringExpired(String key) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断String类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsStringNoPrefix(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(key).isExists();
    }

    /**
     * 删除String类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteString(String key) {
        verifyParameters(key);
        REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_STRING_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除String类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteStringNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_STRING_PREFIX + keyPattern);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除String类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteStringNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(keyPattern);
    }

    /**
     * 设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putNumber(String key, Number value) {
        verifyParameters(key, value);
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
        verifyParameters(key, value);
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
    public static void putNumber(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        bucket.set(value, expired);
    }

    /**
     * 不需要默认前缀，设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putNumberNoPrefix(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key);
        bucket.set(value, expired);
    }

    /**
     * 如果存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberIfExists(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.setIfExists(value);
    }

    /**
     * 不需要默认前缀，如果存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberNoPrefixIfExists(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfExists(value);
    }

    /**
     * 如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putNumberIfExists(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.setIfExists(value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putNumberNoPrefixIfExists(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfExists(value, expired);
    }

    /**
     * 如果不存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberIfAbsent(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.setIfAbsent(value);
    }

    /**
     * 不需要默认前缀，如果不存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberNoPrefixIfAbsent(String key, CharSequence value) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfAbsent(value);
    }

    /**
     * 如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putNumberIfAbsent(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value, expired);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.setIfAbsent(value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberNoPrefixIfAbsent(String key, CharSequence value, Duration expired) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.setIfAbsent(value, expired);
    }

    /**
     * 获取Number类型缓存
     *
     * @param key 缓存键
     */
    public static Number getNumber(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get();
    }

    /**
     * 不需要默认前缀，获取Number类型缓存
     *
     * @param key 缓存键
     */
    public static Number getNumberNoPrefix(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get();
    }

    /**
     * 获取Number中byte类型缓存
     *
     * @param key 缓存键
     */
    public static byte getNumberByte(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().byteValue();
    }

    /**
     * 不需要默认前缀，获取Number中byte类型缓存
     *
     * @param key 缓存键
     */
    public static byte getNumberByteNoPrefix(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get().byteValue();
    }

    /**
     * 获取Number中int类型缓存
     *
     * @param key 缓存键
     */
    public static int getNumberInt(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().intValue();
    }

    /**
     * 不需要默认前缀，获取Number中int类型缓存
     *
     * @param key 缓存键
     */
    public static int getNumberIntNoPrefix(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get().intValue();
    }

    /**
     * 获取Number中short类型缓存
     *
     * @param key 缓存键
     */
    public static short getNumberShort(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().shortValue();
    }

    /**
     * 不需要默认前缀，获取Number中short类型缓存
     *
     * @param key 缓存键
     */
    public static short getNumberShortNoPrefix(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get().shortValue();
    }

    /**
     * 获取Number中long类型缓存
     *
     * @param key 缓存键
     */
    public static long getNumberLong(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().longValue();
    }

    /**
     * 不需要默认前缀，获取Number中long类型缓存
     *
     * @param key 缓存键
     */
    public static long getNumberLongNoPrefix(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get().longValue();
    }

    /**
     * 获取Number中float类型缓存
     *
     * @param key 缓存键
     */
    public static float getNumberFloat(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().floatValue();
    }

    /**
     * 不需要默认前缀，获取Number中float类型缓存
     *
     * @param key 缓存键
     */
    public static float getNumberFloatNoPrefix(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
        return bucket.get().floatValue();
    }

    /**
     * 获取Number中double类型缓存
     *
     * @param key 缓存键
     */
    public static double getNumberDouble(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key);
        return bucket.get().doubleValue();
    }

    /**
     * 不需要默认前缀，获取Number中double类型缓存
     *
     * @param key 缓存键
     */
    public static double getNumberDoubleNoPrefix(String key) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(key);
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
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_NUMBER_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_NUMBER_PREFIX, ""));
        });
        return res;
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Number类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getNumberKeysNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(res::add);
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
        verifyParameters(keyPattern);
        return getNumberKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getNumber(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Number) map.get("value")));
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存Number类型键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, Number> getNumberKeyValuesNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        return getNumberKeysNoPrefixByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getNumberNoPrefix(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Number) map.get("value")));
    }

    /**
     * 获取Number类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getNumberExpired(String key) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断Number类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsNumberNoPrefix(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(key).isExists();
    }

    /**
     * 删除Number类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteNumber(String key) {
        verifyParameters(key);
        REDISSON_CLIENT.getBucket(KeyPrefixConstants.CACHE_NUMBER_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除Number类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteNumberNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_NUMBER_PREFIX + keyPattern);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除Number类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteNumberNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(keyPattern);
    }

    /**
     * 设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putList(String key, List<T> valueList) {
        verifyParameters(key, valueList);
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
        verifyParameters(key, valueList);
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
    public static <T> void putList(String key, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList, expired);
        RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
        list.addAll(valueList);
        list.expire(expired);
    }

    /**
     * 不需要默认前缀，设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putListNoPrefix(String key, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList, expired);
        RList<T> list = REDISSON_CLIENT.getList(key);
        list.addAll(valueList);
        list.expire(expired);
    }

    /**
     * 如果存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> Boolean putListIfExists(String key, List<T> valueList) {
        verifyParameters(key, valueList);
        if (existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            return list.addAll(valueList);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> Boolean putListNoPrefixIfExists(String key, List<T> valueList) {
        verifyParameters(key, valueList);
        if (existsListNoPrefix(key)) {
            RList<T> list = REDISSON_CLIENT.getList(key);
            return list.addAll(valueList);
        } else {
            return false;
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
    public static <T> Boolean putListIfExists(String key, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList, expired);
        if (existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            return list.addAll(valueList) && list.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果存在则设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    public static <T> Boolean putListNoPrefixIfExists(String key, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList, expired);
        if (existsListNoPrefix(key)) {
            RList<T> list = REDISSON_CLIENT.getList(key);
            return list.addAll(valueList) && list.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 如果不存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> Boolean putListIfAbsent(String key, List<T> valueList) {
        verifyParameters(key, valueList);
        if (!existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            return list.addAll(valueList);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果不存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> Boolean putListNoPrefixIfAbsent(String key, List<T> valueList) {
        verifyParameters(key, valueList);
        if (!existsListNoPrefix(key)) {
            RList<T> list = REDISSON_CLIENT.getList(key);
            return list.addAll(valueList);
        } else {
            return false;
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
    public static <T> Boolean putListIfAbsent(String key, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList, expired);
        if (!existsList(key)) {
            RList<T> list = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
            return list.addAll(valueList) && list.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果不存在则设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    public static <T> Boolean putListNoPrefixIfAbsent(String key, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList, expired);
        if (!existsListNoPrefix(key)) {
            RList<T> list = REDISSON_CLIENT.getList(key);
            return list.addAll(valueList) && list.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 获取List类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> List<T> getList(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key);
    }

    /**
     * 不需要默认前缀，获取List类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> List<T> getListNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_LIST_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_LIST_PREFIX, ""));
        });
        return res;
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取List类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getListKeysNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(res::add);
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
        verifyParameters(keyPattern);
        return getListKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getList(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (List<Object>) map.get("value")));
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓List类型存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static Map<String, List<Object>> getListKeyValuesNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        return getListKeysNoPrefixByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getListNoPrefix(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (List<Object>) map.get("value")));
    }

    /**
     * 获取List类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getListExpired(String key) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getList(key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        return REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断List类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsListNoPrefix(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getList(key).isExists();
    }

    /**
     * 删除List类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteList(String key) {
        verifyParameters(key);
        REDISSON_CLIENT.getList(KeyPrefixConstants.CACHE_LIST_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除List类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteListNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_LIST_PREFIX + keyPattern);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除List类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteListNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(keyPattern);
    }

    /**
     * 设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSet(String key, Set<T> valueSet) {
        verifyParameters(key, valueSet);
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
        verifyParameters(key, valueSet);
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
    public static <T> void putSet(String key, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet, expired);
        RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
        set.addAll(valueSet);
        set.expire(expired);
    }

    /**
     * 不需要默认前缀，设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSetNoPrefix(String key, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet, expired);
        RSet<T> set = REDISSON_CLIENT.getSet(key);
        set.addAll(valueSet);
        set.expire(expired);
    }

    /**
     * 如果存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetIfExists(String key, Set<T> valueSet) {
        verifyParameters(key, valueSet);
        if (existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            return set.addAll(valueSet);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetNoPrefixIfExists(String key, Set<T> valueSet) {
        verifyParameters(key, valueSet);
        if (existsSetNoPrefix(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(key);
            return set.addAll(valueSet);
        } else {
            return false;
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
    public static <T> Boolean putSetIfExists(String key, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet, expired);
        if (existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            return set.addAll(valueSet) && set.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果存在则设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param expired  过期时间
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetNoPrefixIfExists(String key, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet, expired);
        if (existsSetNoPrefix(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(key);
            return set.addAll(valueSet) && set.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 如果不存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetIfAbsent(String key, Set<T> valueSet) {
        verifyParameters(key, valueSet);
        if (!existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            return set.addAll(valueSet);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果不存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetNoPrefixIfAbsent(String key, Set<T> valueSet) {
        verifyParameters(key, valueSet);
        if (!existsSetNoPrefix(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(key);
            return set.addAll(valueSet);
        } else {
            return false;
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
    public static <T> Boolean putSetIfAbsent(String key, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet, expired);
        if (!existsSet(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
            return set.addAll(valueSet) && set.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果不存在则设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param expired  过期时间
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetNoPrefixIfAbsent(String key, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet, expired);
        if (!existsSetNoPrefix(key)) {
            RSet<T> set = REDISSON_CLIENT.getSet(key);
            return set.addAll(valueSet) && set.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 获取Set类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> Set<T> getSet(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key);
    }

    /**
     * 不需要默认前缀，获取Set类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> Set<T> getSetNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_SET_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_SET_PREFIX, ""));
        });
        return res;
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Set类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getSetKeysNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(res::add);
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
        verifyParameters(keyPattern);
        return getSetKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getSet(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Set<Object>) map.get("value")));
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Set类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Set<Object>> getSetKeyValuesNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        return getSetKeysNoPrefixByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getSetNoPrefix(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Set<Object>) map.get("value")));
    }

    /**
     * 获取Set类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getSetExpired(String key) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getSet(key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        return REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断Set类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsSetNoPrefix(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getSet(key).isExists();
    }

    /**
     * 删除Set类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteSet(String key) {
        verifyParameters(key);
        REDISSON_CLIENT.getSet(KeyPrefixConstants.CACHE_SET_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除Set类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteSetNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_SET_PREFIX + keyPattern);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除Set类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteSetNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(keyPattern);
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
        verifyParameters(key, valueMap);
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
        verifyParameters(key, valueMap);
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
    public static <K, V> void putMap(String key, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap, expired);
        RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
        map.putAll(valueMap);
        map.expire(expired);
    }

    /**
     * 不需要默认前缀，设置Map类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> void putMapNoPrefix(String key, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap, expired);
        RMap<K, V> map = REDISSON_CLIENT.getMap(key);
        map.putAll(valueMap);
        map.expire(expired);
    }

    /**
     * 如果存在则设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> Boolean putMapIfExists(String key, Map<K, V> valueMap) {
        verifyParameters(key, valueMap);
        if (existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果存在则设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> Boolean putMapNoPrefixIfExists(String key, Map<K, V> valueMap) {
        verifyParameters(key, valueMap);
        if (existsMapNoPrefix(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(key);
            map.putAll(valueMap);
            return true;
        } else {
            return false;
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
    public static <K, V> Boolean putMapIfExists(String key, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap, expired);
        if (existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
            return map.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果存在则设置Map类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param expired  过期时间
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> Boolean putMapNoPrefixIfExists(String key, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap, expired);
        if (existsMapNoPrefix(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(key);
            map.putAll(valueMap);
            return map.expire(expired);
        } else {
            return false;
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
    public static <K, V> Boolean putMapIfAbsent(String key, Map<K, V> valueMap) {
        verifyParameters(key, valueMap);
        if (!existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
            return true;
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果不存在则设置Map类型缓存
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> Boolean putMapNoPrefixIfAbsent(String key, Map<K, V> valueMap) {
        verifyParameters(key, valueMap);
        if (!existsMapNoPrefix(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(key);
            map.putAll(valueMap);
            return true;
        } else {
            return false;
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
    public static <K, V> Boolean putMapIfAbsent(String key, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap, expired);
        if (!existsMap(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key);
            map.putAll(valueMap);
            return map.expire(expired);
        } else {
            return false;
        }
    }

    /**
     * 不需要默认前缀，如果不存在则设置Map类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueMap 缓存值
     * @param expired  过期时间
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    public static <K, V> Boolean putMapNoPrefixIfAbsent(String key, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap, expired);
        if (!existsMapNoPrefix(key)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(key);
            map.putAll(valueMap);
            return map.expire(expired);
        } else {
            return false;
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
        verifyParameters(key);
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
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(KeyPrefixConstants.CACHE_MAP_PREFIX + keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(key -> {
            res.add(key.replaceFirst(KeyPrefixConstants.CACHE_MAP_PREFIX, ""));
        });
        return res;
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Map类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getMapKeysNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        Iterable<String> keysByPattern = keys.getKeysByPattern(keyPattern);
        // 这里使用链表存储键，从理论上尽可能多的存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(res::add);
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
        verifyParameters(keyPattern);
        return getMapKeysByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getMap(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Map<Object, Object>) map.get("value")));
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Map类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Map<Object, Object>> getMapKeyValuesNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        return getMapKeysNoPrefixByPattern(keyPattern).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getMapNoPrefix(c));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Map<Object, Object>) map.get("value")));
    }

    /**
     * 获取Map类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getMapExpired(String key) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getMap(key).getExpireTime();
        if (expireTime <= 0) {
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
        verifyParameters(key);
        return REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key).isExists();
    }

    /**
     * 不需要默认前缀，判断Map类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static boolean existsMapNoPrefix(String key) {
        verifyParameters(key);
        return REDISSON_CLIENT.getMap(key).isExists();
    }

    /**
     * 删除Map类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteMap(String key) {
        verifyParameters(key);
        REDISSON_CLIENT.getMap(KeyPrefixConstants.CACHE_MAP_PREFIX + key).delete();
    }

    /**
     * 不需要默认前缀，删除Map类型缓存
     *
     * @param key 缓存键
     */
    public static void deleteMapNoPrefix(String key) {
        verifyParameters(key);
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
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.CACHE_MAP_PREFIX + keyPattern);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除Map类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteMapNoPrefixByPattern(String keyPattern) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(keyPattern);
    }

    /**
     * 验证参数是否含空
     *
     * @param params 参数
     */
    private static void verifyParameters(Object... params) {
        if (ObjectUtils.anyNull(params)) {
            throw new CustomizeRedissonException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "参数均不能为空");
        }
    }

}