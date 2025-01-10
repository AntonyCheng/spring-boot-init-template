package top.sharehome.springbootinittemplate.utils.redisson.rateLimit;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.redisson.condition.RedissonCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeRedissonException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.rateLimit.model.TimeModel;

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

    /**
     * 初始化参数以及校验参数
     */
    @PostConstruct
    private void initParams() {
        // 程序创建时删除Redis中的工具类限流键值对
        deleteUtilsRateLimitValues();
    }

    /**
     * 程序销毁后删除Redis中的工具类限流键值对
     */
    @PreDestroy
    private void destroy() {
        deleteUtilsRateLimitValues();
    }

    /**
     * 删除Redis中的工具类限流键值对
     */
    private void deleteUtilsRateLimitValues() {
        REDISSON_CLIENT.getKeys().deleteByPattern("{" + KeyPrefixConstants.RATE_LIMIT_UTILS_PREFIX + "*}:permits");
        REDISSON_CLIENT.getKeys().deleteByPattern("{" + KeyPrefixConstants.RATE_LIMIT_UTILS_PREFIX + "*}:value");
        REDISSON_CLIENT.getKeys().deleteByPattern(KeyPrefixConstants.RATE_LIMIT_UTILS_PREFIX + "*");
    }

    /**
     * 限流
     *
     * @param key          区分不同的限流器，比如不同的用户 id 应该分别统计
     * @param rateInterval 限流单位时间
     * @param rate         限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     * @param permit       每个操作所要消耗的令牌数，该参数值不能大于rate参数值
     */
    public static void doRateLimit(String key, TimeModel rateInterval, Long rate, Long permit) {
        if (rate < permit) {
            throw new CustomizeRedissonException(ReturnCode.PARAMETER_FORMAT_MISMATCH, "令牌数不足");
        }
        RRateLimiter rateLimiter = REDISSON_CLIENT.getRateLimiter(KeyPrefixConstants.RATE_LIMIT_UTILS_PREFIX + key);
        rateLimiter.trySetRate(RateType.OVERALL, rate, Duration.ofMillis(rateInterval.toMillis()));
        boolean canOp = rateLimiter.tryAcquire(permit);
        if (!canOp) {
            throw new CustomizeRedissonException(ReturnCode.TOO_MANY_REQUESTS);
        }
    }

    /**
     * 限流并且为限流键值设定过期时间
     *
     * @param key          区分不同的限流器，比如不同的用户 id 应该分别统计
     * @param rateInterval 限流单位时间
     * @param rate         限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     * @param permit       每个操作所要消耗的令牌数，该参数值不能大于rate参数值
     * @param expire       限流器键值过期时间，过期时间不能小于限流单位时间，否则默认使用限流单位时间作为过期时间
     */
    public static void doRateLimitAndExpire(String key, TimeModel rateInterval, Long rate, Long permit, TimeModel expire) {
        doRateLimit(key, rateInterval, rate, permit);
        String baseKey = KeyPrefixConstants.RATE_LIMIT_UTILS_PREFIX + key;
        List<String> uselessCacheKeys = new ArrayList<>() {
            {
                add(baseKey);
                add("{" + baseKey + "}:permits");
                add("{" + baseKey + "}:value");
            }
        };
        for (String uselessCacheKey : uselessCacheKeys) {
            REDISSON_CLIENT.getBucket(uselessCacheKey).expire(Duration.ofMillis(Math.max(expire.toMillis(), rateInterval.toMillis())));
        }
    }

}