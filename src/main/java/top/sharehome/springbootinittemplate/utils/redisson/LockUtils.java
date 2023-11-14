package top.sharehome.springbootinittemplate.utils.redisson;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.config.redisson.condition.RedissonCondition;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeLockException;
import top.sharehome.springbootinittemplate.utils.redisson.constants.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.function.VoidFunction;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 *
 * @author AntonyCheng
 */
@Component
@Conditional(RedissonCondition.class)
public class LockUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    public static void lock(String key, VoidFunction func){
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = lock.tryLock();
        if (lockResult) {
            func.method();
        }

    }

    /**
     * 无论获取锁失败与否均带有自定义类型返回值，非自动释放型，不可等待分布式锁
     *
     * @param key         锁键值
     * @param success     获取锁成功的操作
     * @param fail        获取锁失败的操作
     * @param <T>         返回的类型泛型
     * @return 返回结果
     */
    public static <T> T lock(String key, Supplier<T> success, Supplier<T> fail) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock();
            if (lockResult) {
                return success.get();
            } else {
                return fail.get();
            }
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 无论获取锁失败与否均带有自定义类型返回值，非自动释放型，可等待分布式锁
     *
     * @param key         锁键值
     * @param waitTime    最大等待时间/ms
     * @param success     获取锁成功的操作
     * @param fail        获取锁失败的操作
     * @param <T>         返回的类型泛型
     * @return 返回结果
     */
    public static <T> T lock(String key, long waitTime, Supplier<T> success, Supplier<T> fail) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            if (lockResult) {
                return success.get();
            } else {
                return fail.get();
            }
        } catch (InterruptedException e) {
            throw new CustomizeLockException();
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 无论获取锁失败与否均带有自定义类型返回值，自动释放型，可等待分布式锁
     *
     * @param key         锁键值
     * @param waitTime    最大等待时间/ms
     * @param releaseTime 自动释放时间/ms
     * @param success     获取锁成功的操作
     * @param fail        获取锁失败的操作
     * @param <T>         返回的类型泛型
     * @return 返回结果
     */
    public static <T> T lock(String key, long waitTime, long releaseTime, Supplier<T> success, Supplier<T> fail) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(waitTime, releaseTime, TimeUnit.MILLISECONDS);
            if (lockResult) {
                return success.get();
            } else {
                return fail.get();
            }
        } catch (InterruptedException e) {
            throw new CustomizeLockException();
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
    }

}