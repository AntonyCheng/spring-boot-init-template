package top.sharehome.springbootinittemplate.utils.redisson.cache;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.redisson.api.*;
import org.redisson.api.options.KeysScanOptions;
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
        put0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param <T>   泛型T
     */
    public static <T> void putNoPrefix(String key, T value) {
        put0(key, Boolean.FALSE, value, null);

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
        put0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，设置缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param value     缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    public static <T> void putNoPrefix(String key, T value, Duration expired) {
        put0(key, Boolean.FALSE, value, expired);
    }

    /**
     * put基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> void put0(String key, Boolean hasPrefix, T value, Duration expired) {
        verifyParameters(key, value);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + key : key);
        if (Objects.nonNull(expired)) {
            bucket.set(value, expired);
        } else {
            bucket.set(value);
        }
    }

    /**
     * 如果存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putIfExists(String key, T value) {
        return putIfExists0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，如果存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putNoPrefixIfExists(String key, T value) {
        return putIfExists0(key, Boolean.FALSE, value, null);
    }

    /**
     * 如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putIfExists(String key, T value, Duration expired) {
        return putIfExists0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putNoPrefixIfExists(String key, T value, Duration expired) {
        return putIfExists0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putIfExists基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> Boolean putIfExists0(String key, Boolean hasPrefix, T value, Duration expired) {
        verifyParameters(key, value);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + key : key);
        if (Objects.nonNull(expired)) {
            return bucket.setIfExists(value, expired);
        } else {
            return bucket.setIfExists(value);
        }
    }

    /**
     * 如果不存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putIfAbsent(String key, T value) {
        return putIfAbsent0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，如果不存在则设置缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static <T> Boolean putNoPrefixIfAbsent(String key, T value) {
        return putIfAbsent0(key, Boolean.FALSE, value, null);
    }

    /**
     * 如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putIfAbsent(String key, T value, Duration expired) {
        return putIfAbsent0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static <T> Boolean putNoPrefixIfAbsent(String key, T value, Duration expired) {
        return putIfAbsent0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putIfAbsent基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> Boolean putIfAbsent0(String key, Boolean hasPrefix, T value, Duration expired) {
        verifyParameters(key, value);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + key : key);
        if (Objects.nonNull(expired)) {
            return bucket.setIfAbsent(value, expired);
        } else {
            return bucket.setIfAbsent(value);
        }
    }

    /**
     * 获取缓存
     *
     * @param key 缓存键
     */
    public static Object get(String key) {
        return get0(key, Boolean.TRUE, null);
    }

    /**
     * 不需要默认前缀，获取缓存
     *
     * @param key 缓存键
     */
    public static Object getNoPrefix(String key) {
        return get0(key, Boolean.FALSE, null);
    }

    /**
     * 根据类型获取缓存
     *
     * @param key  缓存键
     * @param type 返回类型
     * @param <T>  泛型T
     */
    public static <T> T get(String key, Class<T> type) {
        return get0(key, Boolean.TRUE, type);
    }

    /**
     * 不需要默认前缀，根据类型获取缓存
     *
     * @param key  缓存键
     * @param type 返回类型
     * @param <T>  泛型T
     */
    public static <T> T getNoPrefix(String key, Class<T> type) {
        return get0(key, Boolean.FALSE, type);
    }

    /**
     * get基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param type      返回类型
     * @param <T>       泛型T
     */
    private static <T> T get0(String key, Boolean hasPrefix, Class<T> type) {
        verifyParameters(key);
        RBucket<T> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + key : key);
        T t = bucket.get();
        if (Objects.isNull(t) || Objects.isNull(type) || type.isInstance(t)) {
            return t;
        } else {
            throw new CustomizeRedissonException(ReturnCode.FAIL, "数据类型不一致");
        }
    }

    /**
     * 使用通配符模糊获取缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getKeysByPattern(String keyPattern) {
        return getKeysByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getKeysByPattern(String keyPattern, Integer limit) {
        return getKeysByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getKeysNoPrefixByPattern(String keyPattern) {
        return getKeysByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getKeysNoPrefixByPattern(String keyPattern, Integer limit) {
        return getKeysByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getKeysByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static List<String> getKeysByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        KeysScanOptions keysScanOptions = KeysScanOptions
                .defaults()
                .type(RType.OBJECT)
                .pattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + keyPattern : keyPattern);
        if (Objects.nonNull(limit)) {
            keysScanOptions.limit(limit);
        }
        Iterable<String> keysByPattern = keys.getKeys(keysScanOptions);
        // 这里使用链表存储键，从理论上尽可能多地存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(Objects.equals(hasPrefix, Boolean.TRUE) ? key -> res.add(key.replaceFirst(KeyPrefixConstants.CACHE_PREFIX, "")) : res::add);
        return res;
    }

    /**
     * 使用通配符模糊获取缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static <T> Map<String, T> getKeyValuesByPattern(String keyPattern) {
        return getKeyValuesByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static <T> Map<String, T> getKeyValuesByPattern(String keyPattern, Integer limit) {
        return getKeyValuesByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static <T> Map<String, T> getKeyValuesNoPrefixByPattern(String keyPattern) {
        return getKeyValuesByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static <T> Map<String, T> getKeyValuesNoPrefixByPattern(String keyPattern, Integer limit) {
        return getKeyValuesByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getKeyValuesByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    @SuppressWarnings("unchecked")
    private static <T> Map<String, T> getKeyValuesByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        return getKeysByPattern0(keyPattern, hasPrefix, limit).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", get0(c, hasPrefix, null));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (T) map.get("value")));
    }

    /**
     * 获取缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getExpired(String key) {
        return getExpired0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getExpiredNoPrefix(String key) {
        return getExpired0(key, Boolean.FALSE);
    }

    /**
     * getExpired基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Long getExpired0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + key : key).getExpireTime();
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
    public static Boolean exists(String key) {
        return exists0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，判断缓存是否存在
     *
     * @param key 缓存键
     */
    public static Boolean existsNoPrefix(String key) {
        return exists0(key, Boolean.FALSE);
    }

    /**
     * exists基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean exists0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + key : key).isExists();
    }

    /**
     * 删除缓存
     *
     * @param key 缓存键
     */
    public static Boolean delete(String key) {
        return delete0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，删除缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteNoPrefix(String key) {
        return delete0(key, Boolean.FALSE);
    }

    /**
     * delete基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean delete0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + key : key).delete();
    }

    /**
     * 使用通配符模糊删除缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteByPattern(String keyPattern) {
        deleteByPattern0(keyPattern, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteNoPrefixByPattern(String keyPattern) {
        deleteByPattern0(keyPattern, Boolean.FALSE);
    }

    /**
     * deleteByPattern基础方法
     *
     * @param keyPattern  key通配符
     * @param hasPrefix  是否默认前缀
     */
    private static void deleteByPattern0(String keyPattern, Boolean hasPrefix) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_PREFIX + keyPattern : keyPattern);
    }

    /**
     * 设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putString(String key, CharSequence value) {
        putString0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putStringNoPrefix(String key, CharSequence value) {
        putString0(key, Boolean.FALSE, value, null);
    }

    /**
     * 设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putString(String key, CharSequence value, Duration expired) {
        putString0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putStringNoPrefix(String key, CharSequence value, Duration expired) {
        putString0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putString基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     */
    private static void putString0(String key, Boolean hasPrefix, CharSequence value, Duration expired) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + key : key, StringCodec.INSTANCE);
        if (Objects.nonNull(expired)) {
            bucket.set(value, expired);
        } else {
            bucket.set(value);
        }
    }

    /**
     * 如果存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringIfExists(String key, CharSequence value) {
        return putStringIfExists0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，如果存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringNoPrefixIfExists(String key, CharSequence value) {
        return putStringIfExists0(key, Boolean.FALSE, value, null);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringIfExists(String key, CharSequence value, Duration expired) {
        return putStringIfExists0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringNoPrefixIfExists(String key, CharSequence value, Duration expired) {
        return putStringIfExists0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putStringIfExists基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     */
    private static Boolean putStringIfExists0(String key, Boolean hasPrefix, CharSequence value, Duration expired) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + key : key, StringCodec.INSTANCE);
        if (Objects.nonNull(expired)) {
            return bucket.setIfExists(value, expired);
        } else {
            return bucket.setIfExists(value);
        }
    }

    /**
     * 如果不存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringIfAbsent(String key, CharSequence value) {
        return putStringIfAbsent0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，如果不存在则设置String类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putStringNoPrefixIfAbsent(String key, CharSequence value) {
        return putStringIfAbsent0(key, Boolean.FALSE, value, null);
    }

    /**
     * 如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringIfAbsent(String key, CharSequence value, Duration expired) {
        return putStringIfAbsent0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置String类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putStringNoPrefixIfAbsent(String key, CharSequence value, Duration expired) {
        return putStringIfAbsent0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putStringIfAbsent基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     */
    private static Boolean putStringIfAbsent0(String key, Boolean hasPrefix, CharSequence value, Duration expired) {
        verifyParameters(key, value);
        RBucket<CharSequence> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + key : key, StringCodec.INSTANCE);
        if (Objects.isNull(expired)) {
            return bucket.setIfAbsent(value);
        } else {
            return bucket.setIfAbsent(value, expired);
        }
    }

    /**
     * 获取String类型缓存
     *
     * @param key 缓存键
     */
    public static String getString(String key) {
        return getString0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取String类型缓存
     *
     * @param key 缓存键
     */
    public static String getStringNoPrefix(String key) {
        return getString0(key, Boolean.FALSE);
    }

    /**
     * getString基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     */
    private static String getString0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        RBucket<String> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + key : key, StringCodec.INSTANCE);
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
        return getStringKeysByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取String类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getStringKeysByPattern(String keyPattern, Integer limit) {
        return getStringKeysByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取String类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getStringKeysNoPrefixByPattern(String keyPattern) {
        return getStringKeysByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取String类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getStringKeysNoPrefixByPattern(String keyPattern, Integer limit) {
        return getStringKeysByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getStringKeysByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static List<String> getStringKeysByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        KeysScanOptions keysScanOptions = KeysScanOptions
                .defaults()
                .type(RType.OBJECT)
                .pattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + keyPattern : keyPattern);
        if (Objects.nonNull(limit)) {
            keysScanOptions.limit(limit);
        }
        Iterable<String> keysByPattern = keys.getKeys(keysScanOptions);
        // 这里使用链表存储键，从理论上尽可能多地存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(Objects.equals(hasPrefix, Boolean.TRUE) ? key -> res.add(key.replaceFirst(KeyPrefixConstants.CACHE_STRING_PREFIX, "")) : res::add);
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
        return getStringKeyValuesByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取String类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, String> getStringKeyValuesByPattern(String keyPattern, Integer limit) {
        return getStringKeyValuesByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取String类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, String> getStringKeyValuesNoPrefixByPattern(String keyPattern) {
        return getStringKeyValuesByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取String类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, String> getStringKeyValuesNoPrefixByPattern(String keyPattern, Integer limit) {
        return getStringKeyValuesByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getStringKeyValuesByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static Map<String, String> getStringKeyValuesByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        return getStringKeysByPattern0(keyPattern, hasPrefix, limit).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getString0(c, hasPrefix));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (String) map.get("value")));
    }

    /**
     * 获取String类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getStringExpired(String key) {
        return getStringExpired0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取String类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getStringExpiredNoPrefix(String key) {
        return getStringExpired0(key, Boolean.FALSE);
    }

    /**
     * getStringExpired基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Long getStringExpired0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + key : key, StringCodec.INSTANCE).getExpireTime();
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
    public static Boolean existsString(String key) {
        return existsString0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，判断String类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static Boolean existsStringNoPrefix(String key) {
        return existsString0(key, Boolean.FALSE);
    }

    /**
     * existsString基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean existsString0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + key : key, StringCodec.INSTANCE).isExists();
    }

    /**
     * 删除String类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteString(String key) {
        return deleteString0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，删除String类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteStringNoPrefix(String key) {
        return deleteString0(key, Boolean.FALSE);
    }

    /**
     * deleteString基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean deleteString0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + key : key, StringCodec.INSTANCE).delete();
    }

    /**
     * 使用通配符模糊删除String类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteStringByPattern(String keyPattern) {
        deleteStringByPattern0(keyPattern, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除String类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteStringNoPrefixByPattern(String keyPattern) {
        deleteStringByPattern0(keyPattern, Boolean.FALSE);
    }

    /**
     * deleteStringByPattern基础方法
     *
     * @param keyPattern  key通配符
     * @param hasPrefix  是否默认前缀
     */
    private static void deleteStringByPattern0(String keyPattern, Boolean hasPrefix) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_STRING_PREFIX + keyPattern : keyPattern);
    }

    /**
     * 设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putNumber(String key, Number value) {
        putNumber0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void putNumberNoPrefix(String key, Number value) {
        putNumber0(key, Boolean.FALSE, value, null);
    }

    /**
     * 设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putNumber(String key, Number value, Duration expired) {
        putNumber0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static void putNumberNoPrefix(String key, Number value, Duration expired) {
        putNumber0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putNumber基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     */
    private static void putNumber0(String key, Boolean hasPrefix, Number value, Duration expired) {
        verifyParameters(key, value);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + key : key);
        if (Objects.nonNull(expired)) {
            bucket.set(value, expired);
        } else {
            bucket.set(value);
        }
    }

    /**
     * 如果存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberIfExists(String key, Number value) {
        return putNumberIfExists0(key, Boolean.TRUE, value, null);
    }

    /**
     * 不需要默认前缀，如果存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberNoPrefixIfExists(String key, Number value) {
        return putNumberIfExists0(key, Boolean.FALSE, value, null);
    }

    /**
     * 如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putNumberIfExists(String key, Number value, Duration expired) {
        return putNumberIfExists0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putNumberNoPrefixIfExists(String key, Number value, Duration expired) {
        return putNumberIfExists0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putNumberIfExists基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     */
    private static Boolean putNumberIfExists0(String key, Boolean hasPrefix, Number value, Duration expired) {
        verifyParameters(key, value);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + key : key);
        if (Objects.nonNull(expired)) {
            return bucket.setIfExists(value, expired);
        } else {
            return bucket.setIfExists(value);
        }
    }

    /**
     * 如果不存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberIfAbsent(String key, Number value) {
        return putNumberIfAbsent0(key, Boolean.TRUE, value, null);

    }

    /**
     * 不需要默认前缀，如果不存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberNoPrefixIfAbsent(String key, Number value) {
        return putNumberIfAbsent0(key, Boolean.FALSE, value, null);
    }

    /**
     * 如果不存在则设置Number类型缓存，同时设置过期时间
     *
     * @param key     缓存键
     * @param value   缓存值
     * @param expired 过期时间
     */
    public static Boolean putNumberIfAbsent(String key, Number value, Duration expired) {
        return putNumberIfAbsent0(key, Boolean.TRUE, value, expired);
    }

    /**
     * 不需要默认前缀，如果不存在则设置Number类型缓存
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static Boolean putNumberNoPrefixIfAbsent(String key, Number value, Duration expired) {
        return putNumberIfAbsent0(key, Boolean.FALSE, value, expired);
    }

    /**
     * putNumberIfAbsent基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param value     缓存值
     * @param expired   过期时间
     */
    private static Boolean putNumberIfAbsent0(String key, Boolean hasPrefix, Number value, Duration expired) {
        verifyParameters(key, value);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + key : key);
        if (Objects.nonNull(expired)) {
            return bucket.setIfAbsent(value, expired);
        } else {
            return bucket.setIfAbsent(value);
        }
    }

    /**
     * 获取Number类型缓存
     *
     * @param key 缓存键
     */
    public static Number getNumber(String key) {
        return getNumber0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取Number类型缓存
     *
     * @param key 缓存键
     */
    public static Number getNumberNoPrefix(String key) {
        return getNumber0(key, Boolean.FALSE);
    }

    /**
     * 获取Number中byte类型缓存
     *
     * @param key 缓存键
     */
    public static Byte getNumberByte(String key) {
        return getNumber0(key, Boolean.TRUE).byteValue();
    }

    /**
     * 不需要默认前缀，获取Number中byte类型缓存
     *
     * @param key 缓存键
     */
    public static Byte getNumberByteNoPrefix(String key) {
        return getNumber0(key, Boolean.FALSE).byteValue();
    }

    /**
     * 获取Number中int类型缓存
     *
     * @param key 缓存键
     */
    public static Integer getNumberInt(String key) {
        return getNumber0(key, Boolean.TRUE).intValue();
    }

    /**
     * 不需要默认前缀，获取Number中int类型缓存
     *
     * @param key 缓存键
     */
    public static Integer getNumberIntNoPrefix(String key) {
        return getNumber0(key, Boolean.FALSE).intValue();
    }

    /**
     * 获取Number中short类型缓存
     *
     * @param key 缓存键
     */
    public static Short getNumberShort(String key) {
        return getNumber0(key, Boolean.TRUE).shortValue();
    }

    /**
     * 不需要默认前缀，获取Number中short类型缓存
     *
     * @param key 缓存键
     */
    public static Short getNumberShortNoPrefix(String key) {
        return getNumber0(key, Boolean.FALSE).shortValue();
    }

    /**
     * 获取Number中long类型缓存
     *
     * @param key 缓存键
     */
    public static Long getNumberLong(String key) {
        return getNumber0(key, Boolean.TRUE).longValue();
    }

    /**
     * 不需要默认前缀，获取Number中long类型缓存
     *
     * @param key 缓存键
     */
    public static Long getNumberLongNoPrefix(String key) {
        return getNumber0(key, Boolean.FALSE).longValue();
    }

    /**
     * 获取Number中float类型缓存
     *
     * @param key 缓存键
     */
    public static Float getNumberFloat(String key) {
        return getNumber0(key, Boolean.TRUE).floatValue();
    }

    /**
     * 不需要默认前缀，获取Number中float类型缓存
     *
     * @param key 缓存键
     */
    public static Float getNumberFloatNoPrefix(String key) {
        return getNumber0(key, Boolean.FALSE).floatValue();
    }

    /**
     * 获取Number中double类型缓存
     *
     * @param key 缓存键
     */
    public static Double getNumberDouble(String key) {
        return getNumber0(key, Boolean.TRUE).doubleValue();
    }

    /**
     * 不需要默认前缀，获取Number中double类型缓存
     *
     * @param key 缓存键
     */
    public static Double getNumberDoubleNoPrefix(String key) {
        return getNumber0(key, Boolean.FALSE).doubleValue();
    }

    /**
     * getNumber基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     */
    private static Number getNumber0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        RBucket<Number> bucket = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + key : key);
        return bucket.get();
    }

    /**
     * 使用通配符模糊获取Number类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getNumberKeysByPattern(String keyPattern) {
        return getNumberKeysByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取Number类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getNumberKeysByPattern(String keyPattern, Integer limit) {
        return getNumberKeysByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Number类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getNumberKeysNoPrefixByPattern(String keyPattern) {
        return getNumberKeysByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Number类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getNumberKeysNoPrefixByPattern(String keyPattern, Integer limit) {
        return getNumberKeysByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getNumberKeysByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static List<String> getNumberKeysByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        KeysScanOptions keysScanOptions = KeysScanOptions
                .defaults()
                .type(RType.OBJECT)
                .pattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + keyPattern : keyPattern);
        if (Objects.nonNull(limit)) {
            keysScanOptions.limit(limit);
        }
        Iterable<String> keysByPattern = keys.getKeys(keysScanOptions);
        // 这里使用链表存储键，从理论上尽可能多地存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(Objects.equals(hasPrefix, Boolean.TRUE) ? key -> res.add(key.replaceFirst(KeyPrefixConstants.CACHE_NUMBER_PREFIX, "")) : res::add);
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
        return getNumberKeyValuesByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取缓存Number类型键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, Number> getNumberKeyValuesByPattern(String keyPattern, Integer limit) {
        return getNumberKeyValuesByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存Number类型键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, Number> getNumberKeyValuesNoPrefixByPattern(String keyPattern) {
        return getNumberKeyValuesByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓存Number类型键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, Number> getNumberKeyValuesNoPrefixByPattern(String keyPattern, Integer limit) {
        return getNumberKeyValuesByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getNumberKeyValuesByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static Map<String, Number> getNumberKeyValuesByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        return getNumberKeysByPattern0(keyPattern, hasPrefix, limit).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getNumber0(c, hasPrefix));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Number) map.get("value")));
    }

    /**
     * 获取Number类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getNumberExpired(String key) {
        return getNumberExpired0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取Number类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getNumberExpiredNoPrefix(String key) {
        return getNumberExpired0(key, Boolean.FALSE);
    }

    /**
     * getNumberExpired基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Long getNumberExpired0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + key : key).getExpireTime();
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
    public static Boolean existsNumber(String key) {
        return existsNumber0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，判断Number类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static Boolean existsNumberNoPrefix(String key) {
        return existsNumber0(key, Boolean.FALSE);
    }

    /**
     * existsNumber基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean existsNumber0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + key : key).isExists();
    }

    /**
     * 删除Number类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteNumber(String key) {
        return deleteNumber0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，删除Number类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteNumberNoPrefix(String key) {
        return deleteNumber0(key, Boolean.FALSE);
    }

    /**
     * deleteNumber基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean deleteNumber0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getBucket(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + key : key).delete();
    }

    /**
     * 使用通配符模糊删除Number类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteNumberByPattern(String keyPattern) {
        deleteNumberByPattern0(keyPattern, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除Number类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteNumberNoPrefixByPattern(String keyPattern) {
        deleteNumberByPattern0(keyPattern, Boolean.FALSE);
    }

    /**
     * deleteNumberByPattern基础方法
     *
     * @param keyPattern  key通配符
     * @param hasPrefix  是否默认前缀
     */
    private static void deleteNumberByPattern0(String keyPattern, Boolean hasPrefix) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_NUMBER_PREFIX + keyPattern : keyPattern);
    }

    /**
     * 设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putList(String key, List<T> valueList) {
        putList0(key, Boolean.TRUE, valueList, null);
    }

    /**
     * 不需要默认前缀，设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putListNoPrefix(String key, List<T> valueList) {
        putList0(key, Boolean.FALSE, valueList, null);
    }

    /**
     * 设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putList(String key, List<T> valueList, Duration expired) {
        putList0(key, Boolean.TRUE, valueList, expired);
    }

    /**
     * 不需要默认前缀，设置List类型缓存，同时设置过期时间
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> void putListNoPrefix(String key, List<T> valueList, Duration expired) {
        putList0(key, Boolean.FALSE, valueList, expired);
    }

    /**
     * putList基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueList 缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> void putList0(String key, Boolean hasPrefix, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList);
        RList<T> list = REDISSON_CLIENT.getList(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + key : key);
        list.addAll(valueList);
        if (Objects.nonNull(expired)) {
            list.expire(expired);
        }
    }

    /**
     * 如果存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> Boolean putListIfExists(String key, List<T> valueList) {
        return putListIfExists0(key, Boolean.TRUE, valueList, null);
    }

    /**
     * 不需要默认前缀，如果存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> Boolean putListNoPrefixIfExists(String key, List<T> valueList) {
        return putListIfExists0(key, Boolean.FALSE, valueList, null);
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
        return putListIfExists0(key, Boolean.TRUE, valueList, expired);
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
        return putListIfExists0(key, Boolean.FALSE, valueList, expired);
    }

    /**
     * putListIfExists基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueList 缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> Boolean putListIfExists0(String key, Boolean hasPrefix, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList);
        if (existsList0(key, hasPrefix)) {
            RList<T> list = REDISSON_CLIENT.getList(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + key : key);
            return Objects.nonNull(expired) ? list.addAll(valueList) && list.expire(expired) : list.addAll(valueList);
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
        return putListIfAbsent0(key, Boolean.TRUE, valueList, null);
    }

    /**
     * 不需要默认前缀，如果不存在则设置List类型缓存
     *
     * @param key       缓存键
     * @param valueList 缓存值
     * @param <T>       泛型T
     */
    public static <T> Boolean putListNoPrefixIfAbsent(String key, List<T> valueList) {
        return putListIfAbsent0(key, Boolean.FALSE, valueList, null);
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
        return putListIfAbsent0(key, Boolean.TRUE, valueList, expired);
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
        return putListIfAbsent0(key, Boolean.FALSE, valueList, expired);
    }

    /**
     * putListIfAbsent基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueList 缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> Boolean putListIfAbsent0(String key, Boolean hasPrefix, List<T> valueList, Duration expired) {
        verifyParameters(key, valueList);
        if (!existsList0(key, hasPrefix)) {
            RList<T> list = REDISSON_CLIENT.getList(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + key : key);
            return Objects.nonNull(expired) ? list.addAll(valueList) && list.expire(expired) : list.addAll(valueList);
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
        return getList0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取List类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> List<T> getListNoPrefix(String key) {
        return getList0(key, Boolean.FALSE);
    }

    /**
     * getList基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param <T>       泛型T
     */
    private static <T> List<T> getList0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getList(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + key : key);
    }

    /**
     * 使用通配符模糊获取List类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getListKeysByPattern(String keyPattern) {
        return getListKeysByPattern0(keyPattern, Boolean.TRUE, null);
    }


    /**
     * 使用通配符模糊获取List类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getListKeysByPattern(String keyPattern, Integer limit) {
        return getListKeysByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取List类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getListKeysNoPrefixByPattern(String keyPattern) {
        return getListKeysByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取List类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getListKeysNoPrefixByPattern(String keyPattern, Integer limit) {
        return getListKeysByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getListKeysByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static List<String> getListKeysByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        KeysScanOptions keysScanOptions = KeysScanOptions
                .defaults()
                .type(RType.LIST)
                .pattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + keyPattern : keyPattern);
        if (Objects.nonNull(limit)) {
            keysScanOptions.limit(limit);
        }
        Iterable<String> keysByPattern = keys.getKeys(keysScanOptions);
        // 这里使用链表存储键，从理论上尽可能多地存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(Objects.equals(hasPrefix, Boolean.TRUE) ? key -> res.add(key.replaceFirst(KeyPrefixConstants.CACHE_LIST_PREFIX, "")) : res::add);
        return res;
    }

    /**
     * 使用通配符模糊获取缓List类型存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, List<Object>> getListKeyValuesByPattern(String keyPattern) {
        return getListKeyValuesByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取缓List类型存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, List<Object>> getListKeyValuesByPattern(String keyPattern, Integer limit) {
        return getListKeyValuesByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓List类型存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, List<Object>> getListKeyValuesNoPrefixByPattern(String keyPattern) {
        return getListKeyValuesByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取缓List类型存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, List<Object>> getListKeyValuesNoPrefixByPattern(String keyPattern, Integer limit) {
        return getListKeyValuesByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getListKeyValuesByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    @SuppressWarnings("unchecked")
    private static Map<String, List<Object>> getListKeyValuesByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        return getListKeysByPattern0(keyPattern, hasPrefix, limit).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getList0(c, hasPrefix));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (List<Object>) map.get("value")));
    }

    /**
     * 获取List类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getListExpired(String key) {
        return getListExpired0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取List类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getListExpiredNoPrefix(String key) {
        return getListExpired0(key, Boolean.FALSE);
    }

    /**
     * getListExpired基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Long getListExpired0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getList(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + key : key).getExpireTime();
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
    public static Boolean existsList(String key) {
        return existsList0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，判断List类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static Boolean existsListNoPrefix(String key) {
        return existsList0(key, Boolean.FALSE);
    }

    /**
     * existsList基础方法
     *
     * @param key        缓存键
     * @param hasPrefix 是否默认前缀
     */
    private static Boolean existsList0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getList(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + key : key).isExists();
    }

    /**
     * 删除List类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteList(String key) {
        return deleteList0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，删除List类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteListNoPrefix(String key) {
        return deleteList0(key, Boolean.FALSE);
    }

    /**
     * deleteList基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean deleteList0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getList(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + key : key).delete();
    }

    /**
     * 使用通配符模糊删除List类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteListByPattern(String keyPattern) {
        deleteListByPattern0(keyPattern, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除List类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteListNoPrefixByPattern(String keyPattern) {
        deleteListByPattern0(keyPattern, Boolean.FALSE);
    }

    /**
     * deleteListByPattern基础方法
     *
     * @param keyPattern  key通配符
     * @param hasPrefix  是否默认前缀
     */
    private static void deleteListByPattern0(String keyPattern, Boolean hasPrefix) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_LIST_PREFIX + keyPattern : keyPattern);
    }

    /**
     * 设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSet(String key, Set<T> valueSet) {
        putSet0(key, Boolean.TRUE, valueSet, null);
    }

    /**
     * 不需要默认前缀，设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSetNoPrefix(String key, Set<T> valueSet) {
        putSet0(key, Boolean.FALSE, valueSet, null);
    }

    /**
     * 设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSet(String key, Set<T> valueSet, Duration expired) {
        putSet0(key, Boolean.TRUE, valueSet, expired);
    }

    /**
     * 不需要默认前缀，设置Set类型缓存，同时设置过期时间
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> void putSetNoPrefix(String key, Set<T> valueSet, Duration expired) {
        putSet0(key, Boolean.FALSE, valueSet, expired);
    }

    /**
     * putSet基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueSet  缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> void putSet0(String key, Boolean hasPrefix, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet);
        RSet<T> set = REDISSON_CLIENT.getSet(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + key : key);
        set.addAll(valueSet);
        if (Objects.nonNull(expired)) {
            set.expire(expired);
        }
    }

    /**
     * 如果存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetIfExists(String key, Set<T> valueSet) {
        return putSetIfExists0(key, Boolean.TRUE, valueSet, null);
    }

    /**
     * 不需要默认前缀，如果存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetNoPrefixIfExists(String key, Set<T> valueSet) {
        return putSetIfExists0(key, Boolean.FALSE, valueSet, null);
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
        return putSetIfExists0(key, Boolean.TRUE, valueSet, expired);
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
        return putSetIfExists0(key, Boolean.FALSE, valueSet, expired);
    }

    /**
     * putSetIfExists基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueSet  缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> Boolean putSetIfExists0(String key, Boolean hasPrefix, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet);
        if (existsSet0(key, hasPrefix)) {
            RSet<T> set = REDISSON_CLIENT.getSet(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + key : key);
            return Objects.nonNull(expired) ? set.addAll(valueSet) && set.expire(expired) : set.addAll(valueSet);
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
        return putSetIfAbsent0(key, Boolean.TRUE, valueSet, null);
    }

    /**
     * 不需要默认前缀，如果不存在则设置Set类型缓存
     *
     * @param key      缓存键
     * @param valueSet 缓存值
     * @param <T>      泛型T
     */
    public static <T> Boolean putSetNoPrefixIfAbsent(String key, Set<T> valueSet) {
        return putSetIfAbsent0(key, Boolean.FALSE, valueSet, null);
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
        return putSetIfAbsent0(key, Boolean.TRUE, valueSet, expired);
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
        return putSetIfAbsent0(key, Boolean.FALSE, valueSet, expired);
    }

    /**
     * putSetIfAbsent基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueSet  缓存值
     * @param expired   过期时间
     * @param <T>       泛型T
     */
    private static <T> Boolean putSetIfAbsent0(String key, Boolean hasPrefix, Set<T> valueSet, Duration expired) {
        verifyParameters(key, valueSet);
        if (!existsSet0(key, hasPrefix)) {
            RSet<T> set = REDISSON_CLIENT.getSet(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + key : key);
            return Objects.nonNull(expired) ? set.addAll(valueSet) && set.expire(expired) : set.addAll(valueSet);
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
        return getSet0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取Set类型缓存
     *
     * @param key 缓存键
     * @param <T> 泛型T
     */
    public static <T> Set<T> getSetNoPrefix(String key) {
        return getSet0(key, Boolean.FALSE);
    }

    /**
     * getSet基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param <T>       泛型T
     */
    private static <T> Set<T> getSet0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getSet(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + key : key);
    }

    /**
     * 使用通配符模糊获取Set类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getSetKeysByPattern(String keyPattern) {
        return getSetKeysByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取Set类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getSetKeysByPattern(String keyPattern, Integer limit) {
        return getSetKeysByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Set类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getSetKeysNoPrefixByPattern(String keyPattern) {
        return getSetKeysByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Set类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getSetKeysNoPrefixByPattern(String keyPattern, Integer limit) {
        return getSetKeysByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getSetKeysByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static List<String> getSetKeysByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        KeysScanOptions keysScanOptions = KeysScanOptions
                .defaults()
                .type(RType.SET)
                .pattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + keyPattern : keyPattern);
        if (Objects.nonNull(limit)) {
            keysScanOptions.limit(limit);
        }
        Iterable<String> keysByPattern = keys.getKeys(keysScanOptions);
        // 这里使用链表存储键，从理论上尽可能多地存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(Objects.equals(hasPrefix, Boolean.TRUE) ? key -> res.add(key.replaceFirst(KeyPrefixConstants.CACHE_SET_PREFIX, "")) : res::add);
        return res;
    }

    /**
     * 使用通配符模糊获取Set类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, Set<Object>> getSetKeyValuesByPattern(String keyPattern) {
        return getSetKeyValuesByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取Set类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, Set<Object>> getSetKeyValuesByPattern(String keyPattern, Integer limit) {
        return getSetKeyValuesByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Set类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, Set<Object>> getSetKeyValuesNoPrefixByPattern(String keyPattern) {
        return getSetKeyValuesByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Set类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, Set<Object>> getSetKeyValuesNoPrefixByPattern(String keyPattern, Integer limit) {
        return getSetKeyValuesByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getSetKeyValuesByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Set<Object>> getSetKeyValuesByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        return getSetKeysByPattern0(keyPattern, hasPrefix, limit).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getSet0(c, hasPrefix));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Set<Object>) map.get("value")));
    }

    /**
     * 获取Set类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getSetExpired(String key) {
        return getSetExpired0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取Set类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getSetExpiredNoPrefix(String key) {
        return getSetExpired0(key, Boolean.FALSE);
    }

    /**
     * getSetExpired基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Long getSetExpired0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getSet(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + key : key).getExpireTime();
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
    public static Boolean existsSet(String key) {
        return existsSet0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，判断Set类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static Boolean existsSetNoPrefix(String key) {
        return existsSet0(key, Boolean.FALSE);
    }

    /**
     * existsSet基础方法
     *
     * @param key        缓存键
     * @param hasPrefix 是否默认前缀
     */
    private static Boolean existsSet0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getSet(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + key : key).isExists();
    }

    /**
     * 删除Set类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteSet(String key) {
        return deleteSet0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，删除Set类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteSetNoPrefix(String key) {
        return deleteSet0(key, Boolean.FALSE);
    }

    /**
     * deleteList基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean deleteSet0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getSet(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + key : key).delete();
    }

    /**
     * 使用通配符模糊删除Set类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteSetByPattern(String keyPattern) {
        deleteSetByPattern0(keyPattern, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除Set类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteSetNoPrefixByPattern(String keyPattern) {
        deleteSetByPattern0(keyPattern, Boolean.FALSE);
    }

    /**
     * deleteSetByPattern基础方法
     *
     * @param keyPattern  key通配符
     * @param hasPrefix  是否默认前缀
     */
    private static void deleteSetByPattern0(String keyPattern, Boolean hasPrefix) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_SET_PREFIX + keyPattern : keyPattern);
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
        putMap0(key, Boolean.TRUE, valueMap, null);
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
        putMap0(key, Boolean.FALSE, valueMap, null);
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
        putMap0(key, Boolean.TRUE, valueMap, expired);
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
        putMap0(key, Boolean.FALSE, valueMap, expired);
    }

    /**
     * putMap基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueMap  缓存值
     * @param expired   过期时间
     * @param <K>       泛型K
     * @param <V>       泛型V
     */
    private static <K, V> void putMap0(String key, Boolean hasPrefix, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap);
        RMap<K, V> map = REDISSON_CLIENT.getMap(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + key : key);
        map.putAll(valueMap);
        if (Objects.nonNull(expired)) {
            map.expire(expired);
        }
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
        return putMapIfExists0(key, Boolean.TRUE, valueMap, null);
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
        return putMapIfExists0(key, Boolean.FALSE, valueMap, null);
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
        return putMapIfExists0(key, Boolean.TRUE, valueMap, expired);
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
        return putMapIfExists0(key, Boolean.FALSE, valueMap, expired);
    }

    /**
     * putMapIfExists基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueMap  缓存值
     * @param expired   过期时间
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    private static <K, V> Boolean putMapIfExists0(String key, Boolean hasPrefix, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap);
        if (existsMap0(key, hasPrefix)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + key : key);
            map.putAll(valueMap);
            return Objects.isNull(expired) || map.expire(expired);
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
        return putMapIfAbsent0(key, Boolean.TRUE, valueMap, null);
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
        return putMapIfAbsent0(key, Boolean.FALSE, valueMap, null);
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
        return putMapIfAbsent0(key, Boolean.TRUE, valueMap, expired);
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
        return putMapIfAbsent0(key, Boolean.FALSE, valueMap, expired);
    }

    /**
     * putMapIfAbsent基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param valueMap  缓存值
     * @param expired   过期时间
     * @param <K>      泛型K
     * @param <V>      泛型V
     */
    private static <K, V> Boolean putMapIfAbsent0(String key, Boolean hasPrefix, Map<K, V> valueMap, Duration expired) {
        verifyParameters(key, valueMap);
        if (!existsMap0(key, hasPrefix)) {
            RMap<K, V> map = REDISSON_CLIENT.getMap(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + key : key);
            map.putAll(valueMap);
            return Objects.isNull(expired) || map.expire(expired);
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
        return getMap0(key, Boolean.TRUE);

    }

    /**
     * 不需要默认前缀，获取Map类型缓存
     *
     * @param key 缓存键
     * @param <K> 泛型K
     * @param <V> 泛型V
     */
    public static <K, V> Map<K, V> getMapNoPrefix(String key) {
        return getMap0(key, Boolean.FALSE);
    }

    /**
     * getMap基础方法
     *
     * @param key       缓存键
     * @param hasPrefix 是否默认前缀
     * @param <K> 泛型K
     * @param <V> 泛型V
     */
    private static <K, V> Map<K, V> getMap0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        RMap<K, V> map = REDISSON_CLIENT.getMap(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + key : key);
        return new HashMap<>(map);
    }

    /**
     * 使用通配符模糊获取Map类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getMapKeysByPattern(String keyPattern) {
        return getMapKeysByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取Map类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getMapKeysByPattern(String keyPattern, Integer limit) {
        return getMapKeysByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Map类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static List<String> getMapKeysNoPrefixByPattern(String keyPattern) {
        return getMapKeysByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Map类型缓存键
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static List<String> getMapKeysNoPrefixByPattern(String keyPattern, Integer limit) {
        return getMapKeysByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getMapKeysByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    private static List<String> getMapKeysByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        RKeys keys = REDISSON_CLIENT.getKeys();
        KeysScanOptions keysScanOptions = KeysScanOptions
                .defaults()
                .type(RType.MAP)
                .pattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + keyPattern : keyPattern);
        if (Objects.nonNull(limit)) {
            keysScanOptions.limit(limit);
        }
        Iterable<String> keysByPattern = keys.getKeys(keysScanOptions);
        // 这里使用链表存储键，从理论上尽可能多地存储键
        List<String> res = new LinkedList<>();
        keysByPattern.forEach(Objects.equals(hasPrefix, Boolean.TRUE) ? key -> res.add(key.replaceFirst(KeyPrefixConstants.CACHE_MAP_PREFIX, "")) : res::add);
        return res;
    }

    /**
     * 使用通配符模糊获取Map类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, Map<Object, Object>> getMapKeyValuesByPattern(String keyPattern) {
        return getMapKeyValuesByPattern0(keyPattern, Boolean.TRUE, null);
    }

    /**
     * 使用通配符模糊获取Map类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, Map<Object, Object>> getMapKeyValuesByPattern(String keyPattern, Integer limit) {
        return getMapKeyValuesByPattern0(keyPattern, Boolean.TRUE, limit);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Map类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static Map<String, Map<Object, Object>> getMapKeyValuesNoPrefixByPattern(String keyPattern) {
        return getMapKeyValuesByPattern0(keyPattern, Boolean.FALSE, null);
    }

    /**
     * 不需要默认前缀，使用通配符模糊获取Map类型缓存键值对
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     * @param limit      限制数量
     */
    public static Map<String, Map<Object, Object>> getMapKeyValuesNoPrefixByPattern(String keyPattern, Integer limit) {
        return getMapKeyValuesByPattern0(keyPattern, Boolean.FALSE, limit);
    }

    /**
     * getMapKeyValuesByPattern基础方法
     *
     * @param keyPattern key通配符
     * @param hasPrefix  是否默认前缀
     * @param limit      限制数量
     */
    @SuppressWarnings("unchecked")
    private static Map<String, Map<Object, Object>> getMapKeyValuesByPattern0(String keyPattern, Boolean hasPrefix, Integer limit) {
        verifyParameters(keyPattern);
        return getMapKeysByPattern0(keyPattern, hasPrefix, limit).stream().map(c -> {
            HashMap<String, Object> hashMap = new LinkedHashMap<>();
            hashMap.put("key", c);
            hashMap.put("value", getMap0(c, hasPrefix));
            return hashMap;
        }).collect(Collectors.toMap(map -> (String) map.get("key"), map -> (Map<Object, Object>) map.get("value")));
    }

    /**
     * 获取Map类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getMapExpired(String key) {
        return getMapExpired0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，获取Map类型缓存剩余过期时间
     *
     * @param key 缓存键
     */
    public static Long getMapExpiredNoPrefix(String key) {
        return getMapExpired0(key, Boolean.FALSE);
    }

    /**
     * getMapExpired基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Long getMapExpired0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        long expireTime = REDISSON_CLIENT.getMap(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + key : key).getExpireTime();
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
    public static Boolean existsMap(String key) {
        return existsMap0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，判断Map类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static Boolean existsMapNoPrefix(String key) {
        return existsMap0(key, Boolean.FALSE);
    }

    /**
     * 判断Map类型缓存是否存在
     *
     * @param key 缓存键
     */
    public static Boolean existsMap0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getMap(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + key : key).isExists();
    }

    /**
     * 删除Map类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteMap(String key) {
        return deleteMap0(key, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，删除Map类型缓存
     *
     * @param key 缓存键
     */
    public static Boolean deleteMapNoPrefix(String key) {
        return deleteMap0(key, Boolean.FALSE);
    }

    /**
     * deleteMap基础方法
     *
     * @param key        缓存键
     * @param hasPrefix  是否默认前缀
     */
    private static Boolean deleteMap0(String key, Boolean hasPrefix) {
        verifyParameters(key);
        return REDISSON_CLIENT.getMap(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + key : key).delete();
    }

    /**
     * 使用通配符模糊删除Map类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteMapByPattern(String keyPattern) {
        deleteMapByPattern0(keyPattern, Boolean.TRUE);
    }

    /**
     * 不需要默认前缀，使用通配符模糊删除Map类型缓存
     * * 表示匹配零个或多个字符。
     * ? 表示匹配一个字符。
     *
     * @param keyPattern key通配符
     */
    public static void deleteMapNoPrefixByPattern(String keyPattern) {
        deleteMapByPattern0(keyPattern, Boolean.FALSE);
    }

    /**
     * deleteMapByPattern基础方法
     *
     * @param keyPattern  key通配符
     * @param hasPrefix  是否默认前缀
     */
    private static void deleteMapByPattern0(String keyPattern, Boolean hasPrefix) {
        verifyParameters(keyPattern);
        REDISSON_CLIENT.getKeys().deleteByPattern(Objects.equals(hasPrefix, Boolean.TRUE) ? KeyPrefixConstants.CACHE_MAP_PREFIX + keyPattern : keyPattern);
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