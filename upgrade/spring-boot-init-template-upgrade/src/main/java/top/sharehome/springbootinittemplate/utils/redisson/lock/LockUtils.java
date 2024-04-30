package top.sharehome.springbootinittemplate.utils.redisson.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeLockException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.lock.function.SuccessFunction;
import top.sharehome.springbootinittemplate.utils.redisson.lock.function.VoidFunction;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * 分布式锁工具类
 * 工具类中包含三种情况，在使用时针对同一业务逻辑或线程建议采纳相同的情况：
 * 1、无论获取锁成功与否均无返回值
 * 2、无论获取锁成功与否均带有boolean类型返回值
 * 3、无论获取锁成功与否均带有自定义类型返回值
 * 使用该工具类之前需要了解工具类中所设计和涉及的一些概念：
 * 1、看门狗释放：看门狗是Redisson分布式锁中设计的一个防宕机死锁机制，宕机死锁指获取锁之后程序没有释放锁就停止运行而导致其他程序拿不到锁的情况，
 * 看门狗机制就起到一个监听程序健康状态的作用，默认宕机30秒后自动释放锁。
 * 2、自动释放：自动释放需要靠开发者自定义自动释放时间，只要没有设定该时间，那么这个锁就采用看门狗释放，自动释放的存在可提高代码可自定义性，但是
 * 容易产生异常，比如A、B线程均需要运行10s，A线程先运行，B线程等待锁，在5s时A线程自动释放了锁，B线程立即拿到锁开始运行，在10s时A结束运行，工
 * 具类代码逻辑要求A线程释放锁，但是锁在B线程上，所以就会报出异常，或许一些业务需要以上所描述的逻辑，但是这里为了保持以锁为中心的工具类，强制要
 * 求获取锁和释放锁必须在同一线程中操作，即A线程不能释放B线程的锁，如果开发者诚心想实现上述逻辑，请自己编写相关代码，建议使用Redisson缓存或者
 * 延迟队列。
 * 3、同步等待：同步等待主要针对于“无论获取锁成功与否均无返回值”的情况，因为通过传参没有判断处理该线程是否获取得到锁，所以使用此类方法，多个线程
 * 是同步执行的，执行次序取决于线程抢占锁的能力，越强越先执行。
 * 4、不可等待：不可等待主要针对于“无论获取锁成功与否均带有自定义/boolean类型返回值”中不含有waitTime形参的情况，其实它是同步等待的一种，只不
 * 过此类方法能够处理或者忽略没获取到锁的情况，即拿不到锁就不拿，所以不用等待锁也能执行业务逻辑。
 * 5、自定义等待：自定义等待主要针对于“无论获取锁成功与否均带有自定义/boolean类型返回值”中含有waitTime形参的情况，主要是考虑到网络原因和其他
 * 外部因素，如果比较偏向于获取锁成功所要执行的操作，可以在此类方法中设置合理的等待时间，总之自定义等待完全依赖业务逻辑的需求。
 *
 * @author AntonyCheng
 */
@Slf4j
public class LockUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    /**
     * 无论获取锁成功与否均无返回值，看门狗释放型，同步等待分布式锁
     *
     * @param key       锁键值
     * @param eventFunc 获取锁之后的操作
     */
    public static void lockEvent(String key, VoidFunction eventFunc) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        try {
            lock.lock();
            eventFunc.method();
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    /**
     * 无论获取锁成功与否均无返回值，自动释放型，同步等待分布式锁
     *
     * @param key       锁键值
     * @param leaseTime 自动释放时间/ms
     * @param eventFunc 执行的操作
     */
    public static void lockEvent(String key, long leaseTime, VoidFunction eventFunc) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        try {
            lock.lock(leaseTime, TimeUnit.MILLISECONDS);
            eventFunc.method();
        } finally {
            if (lock.isLocked()) {
                lock.unlock();
            }
        }
    }

    /**
     * 无论获取锁成功与否均带有boolean类型返回值，看门狗释放型，不可等待分布式锁
     *
     * @param key     锁键值
     * @param success 获取锁成功的操作
     * @return 返回结果
     */
    public static boolean lockEvent(String key, SuccessFunction success) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock();
            if (lockResult) {
                success.method();
            }
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
        return lockResult;
    }

    /**
     * 无论获取锁成功与否均带有boolean类型返回值，看门狗释放型，自定义等待分布式锁
     *
     * @param key      锁键值
     * @param waitTime 最大等待时间/ms
     * @param success  获取锁成功的操作
     * @return 返回结果
     */
    public static boolean lockEvent(String key, long waitTime, SuccessFunction success) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            if (lockResult) {
                success.method();
            }
        } catch (InterruptedException e) {
            throw new CustomizeLockException(ReturnCode.LOCK_SERVICE_ERROR);
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
        return lockResult;
    }

    /**
     * 无论获取锁成功与否均带有boolean类型返回值，自动释放型，自定义等待分布式锁
     *
     * @param key       锁键值
     * @param waitTime  最大等待时间/ms
     * @param leaseTime 自动释放时间/ms
     * @param success   获取锁成功的操作
     * @return 返回结果
     */
    public static boolean lockEvent(String key, long waitTime, long leaseTime, SuccessFunction success) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (lockResult) {
                success.method();
            }
        } catch (InterruptedException e) {
            throw new CustomizeLockException(ReturnCode.LOCK_SERVICE_ERROR);
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
        return lockResult;
    }

    /**
     * 无论获取锁成功与否均带有自定义类型返回值，看门狗释放型，不可等待分布式锁
     *
     * @param key     锁键值
     * @param getLock 获取锁成功的操作
     * @param getNone 获取锁失败的操作
     * @param <T>     返回的类型泛型
     * @return 返回结果
     */
    public static <T> T lockEvent(String key, Supplier<T> getLock, Supplier<T> getNone) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock();
            if (lockResult) {
                return getLock.get();
            } else {
                return getNone.get();
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
     * 无论获取锁成功与否均带有自定义类型返回值，看门狗释放型，自定义等待分布式锁
     *
     * @param key      锁键值
     * @param waitTime 最大等待时间/ms
     * @param getLock  获取锁成功的操作
     * @param getNone  获取锁失败的操作
     * @param <T>      返回的类型泛型
     * @return 返回结果
     */
    public static <T> T lockEvent(String key, long waitTime, Supplier<T> getLock, Supplier<T> getNone) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(waitTime, TimeUnit.MILLISECONDS);
            if (lockResult) {
                return getLock.get();
            } else {
                return getNone.get();
            }
        } catch (InterruptedException e) {
            throw new CustomizeLockException(ReturnCode.LOCK_SERVICE_ERROR);
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 无论获取锁成功与否均带有自定义类型返回值，自动释放型，自定义等待分布式锁
     *
     * @param key       锁键值
     * @param waitTime  最大等待时间/ms
     * @param leaseTime 自动释放时间/ms
     * @param getLock   获取锁成功的操作
     * @param getNone   获取锁失败的操作
     * @param <T>       返回的类型泛型
     * @return 返回结果
     */
    public static <T> T lockEvent(String key, long waitTime, long leaseTime, Supplier<T> getLock, Supplier<T> getNone) {
        RLock lock = REDISSON_CLIENT.getLock(KeyPrefixConstants.LOCK_PREFIX + key);
        boolean lockResult = false;
        try {
            lockResult = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (lockResult) {
                return getLock.get();
            } else {
                return getNone.get();
            }
        } catch (InterruptedException e) {
            throw new CustomizeLockException(ReturnCode.LOCK_SERVICE_ERROR);
        } finally {
            if (lockResult) {
                if (lock.isLocked()) {
                    lock.unlock();
                }
            }
        }
    }

}