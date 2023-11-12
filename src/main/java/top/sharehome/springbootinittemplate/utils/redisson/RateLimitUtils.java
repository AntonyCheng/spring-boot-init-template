package top.sharehome.springbootinittemplate.utils.redisson;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.redisson.constants.CachePrefixConstants;

/**
 * 限流工具类
 *
 * @author AntonyCheng
 */
@Component
@ConditionalOnProperty(value = {"redisson.singleServerConfig.enableSingle", "redisson.clusterServersConfig.enableCluster"}, havingValue = "true")
public class RateLimitUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    private int rate = 2;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public static void doRateLimit(String key) {
        // 创建一个名称为user_limiter的限流器，每秒最多访问 2 次
        RRateLimiter rateLimiter = REDISSON_CLIENT.getRateLimiter(CachePrefixConstants.RATE_LIMIT_PREFIX + key);
        rateLimiter.trySetRate(RateType.OVERALL, 2, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        if (!canOp) {
            throw new CustomizeReturnException(ReturnCode.TOO_MANY_REQUESTS);
        }
    }

}