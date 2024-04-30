package top.sharehome.springbootinittemplate.utils.redisson.rateLimit;

import lombok.AllArgsConstructor;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.redisson.condition.RedissonCondition;
import top.sharehome.springbootinittemplate.config.redisson.properties.RedissonProperties;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeLockException;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * 限流工具类
 *
 * @author AntonyCheng
 */
@Component
@AllArgsConstructor
@Conditional(RedissonCondition.class)
public class RateLimitUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    private final RedissonProperties redissonProperties;

    /**
     * 限流单位时间，单位：秒
     */
    private static long rateInterval;

    /**
     * 限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     */
    private static long rate;

    /**
     * 每个操作所要消耗的令牌数
     */
    private static long permit;

    /**
     * 初始化参数以及校验参数
     */
    @PostConstruct
    private void initParams() {
        rateInterval = redissonProperties.getLimitRateInterval();
        rate = redissonProperties.getLimitRate();
        permit = redissonProperties.getLimitPermits();
        if (permit > rate) {
            throw new CustomizeLockException(ReturnCode.LOCK_DESIGN_ERROR);
        }
        // 程序创建时删除Redis中的系统限流键值对
        deleteSystemRateLimitValues();
    }

    /**
     * 程序销毁后删除Redis中的系统限流键值对
     */
    @PreDestroy
    private void destroy() {
        deleteSystemRateLimitValues();
    }

    /**
     * 删除删除Redis中的系统限流键值对
     */
    private void deleteSystemRateLimitValues() {
        REDISSON_CLIENT.getKeys().deleteByPattern("{" + KeyPrefixConstants.SYSTEM_RATE_LIMIT_PREFIX + "*}:permits");
        REDISSON_CLIENT.getKeys().deleteByPattern("{" + KeyPrefixConstants.SYSTEM_RATE_LIMIT_PREFIX + "*}:value");
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.SYSTEM_RATE_LIMIT_PREFIX + "*");
    }

    /**
     * 系统限流
     * 系统：指的是按照在配置文件中配置好的参数进行限流
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public static void doRateLimit(String key) {
        // 创建一个限流器，默认每秒最多访问 2 次
        RRateLimiter rateLimiter = REDISSON_CLIENT.getRateLimiter(KeyPrefixConstants.SYSTEM_RATE_LIMIT_PREFIX + key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，默认请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(permit);
        if (!canOp) {
            throw new CustomizeReturnException(ReturnCode.TOO_MANY_REQUESTS);
        }
    }

    /**
     * 自定义限流
     * 自定义：指的是开发者可以输入实参进行限流
     *
     * @param key          区分不同的限流器，比如不同的用户 id 应该分别统计
     * @param rateInterval 限流单位时间，单位：秒
     * @param rate         限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     * @param permit       每个操作所要消耗的令牌数
     */
    public static void doRateLimit(String key, Long rateInterval, Long rate, Long permit) {
        RRateLimiter rateLimiter = REDISSON_CLIENT.getRateLimiter(KeyPrefixConstants.CUSTOMIZE_RATE_LIMIT_PREFIX + key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
        boolean canOp = rateLimiter.tryAcquire(permit);
        if (!canOp) {
            throw new CustomizeReturnException(ReturnCode.TOO_MANY_REQUESTS);
        }
    }

    /**
     * 系统限流并且为限流键值设定过期时间
     * 系统：指的是按照在配置文件中配置好的参数进行限流
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public static void doRateLimitAndExpire(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = REDISSON_CLIENT.getRateLimiter(KeyPrefixConstants.SYSTEM_RATE_LIMIT_PREFIX + key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(permit);
        if (!canOp) {
            throw new CustomizeReturnException(ReturnCode.TOO_MANY_REQUESTS);
        }
        String baseKey = KeyPrefixConstants.SYSTEM_RATE_LIMIT_PREFIX + key;
        List<String> uselessCacheKeys = new ArrayList<String>() {
            {
                add(baseKey);
                add("{" + baseKey + "}:permits");
                add("{" + baseKey + "}:value");
            }
        };
        for (String uselessCacheKey : uselessCacheKeys) {
            REDISSON_CLIENT.getBucket(uselessCacheKey).expire(Duration.ofSeconds(rate * 3));
        }
    }

    /**
     * 自定义限流并且为限流键值设定过期时间
     * 自定义：指的是开发者可以输入实参进行限流
     *
     * @param key          区分不同的限流器，比如不同的用户 id 应该分别统计
     * @param rateInterval 限流单位时间，单位：秒
     * @param rate         限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     * @param permit       每个操作所要消耗的令牌数
     */
    public static void doRateLimitAndExpire(String key, Long rateInterval, Long rate, Long permit) {
        RRateLimiter rateLimiter = REDISSON_CLIENT.getRateLimiter(KeyPrefixConstants.CUSTOMIZE_RATE_LIMIT_PREFIX + key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, rateInterval, RateIntervalUnit.SECONDS);
        boolean canOp = rateLimiter.tryAcquire(permit);
        if (!canOp) {
            throw new CustomizeReturnException(ReturnCode.TOO_MANY_REQUESTS);
        }
        String baseKey = KeyPrefixConstants.CUSTOMIZE_RATE_LIMIT_PREFIX + key;
        List<String> uselessCacheKeys = new ArrayList<String>() {
            {
                add(baseKey);
                add("{" + baseKey + "}:permits");
                add("{" + baseKey + "}:value");
            }
        };
        for (String uselessCacheKey : uselessCacheKeys) {
            REDISSON_CLIENT.getBucket(uselessCacheKey).expire(Duration.ofSeconds(rate * 3));
        }
    }

}