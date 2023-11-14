package top.sharehome.springbootinittemplate.utils.redisson;

import org.redisson.api.RedissonClient;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;

/**
 * 分布式锁工具类
 *
 * @author AntonyCheng
 */
public class LockUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

}