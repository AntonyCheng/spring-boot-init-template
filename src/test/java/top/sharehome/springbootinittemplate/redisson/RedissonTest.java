package top.sharehome.springbootinittemplate.redisson;

import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeRedissonException;
import top.sharehome.springbootinittemplate.utils.redisson.cache.CacheUtils;
import top.sharehome.springbootinittemplate.utils.redisson.lock.LockUtils;
import top.sharehome.springbootinittemplate.utils.redisson.lock.function.SuccessFunction;
import top.sharehome.springbootinittemplate.utils.redisson.lock.function.VoidFunction;
import top.sharehome.springbootinittemplate.utils.redisson.queue.QueueUtils;
import top.sharehome.springbootinittemplate.utils.redisson.rateLimit.RateLimitUtils;
import top.sharehome.springbootinittemplate.utils.redisson.rateLimit.model.TimeModel;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * 测试缓存类
 *
 * @author AntonyCheng
 */
@SpringBootTest
public class RedissonTest {

    /**
     * 测试缓存工具类
     */
    @Test
    void testCacheUtils() {
        // 测试字符串
        CacheUtils.putString("test", "test");
        System.out.println(CacheUtils.getString("test"));
        System.out.println(CacheUtils.existsString("test"));
        CacheUtils.deleteString("test");

        // 测试数字
        CacheUtils.putNumber("test", 9999999999L);
        System.out.println(CacheUtils.getNumberDouble("test"));
        System.out.println(CacheUtils.existsNumber("test"));
        CacheUtils.deleteNumber("test");

        // 测试List
        List<String> l = new ArrayList<>() {
            {
                add("test1");
                add("test2");
            }
        };
        CacheUtils.putList("test", l);
        System.out.println(CacheUtils.getList("test"));
        System.out.println(CacheUtils.existsList("test"));
        CacheUtils.deleteList("test");

        // 测试Set
        Set<String> s = new HashSet<>() {
            {
                add("test1");
                add("test2");
            }
        };
        CacheUtils.putSet("test", s);
        System.out.println(CacheUtils.getSet("test"));
        System.out.println(CacheUtils.existsSet("test"));
        CacheUtils.deleteSet("test");

        // 测试Map
        Map<String, String> m = new HashMap<>() {
            {
                put("test1", "test1");
                put("test2", "test2");
            }
        };
        CacheUtils.putMap("test", m);
        System.out.println(CacheUtils.getMap("test"));
        System.out.println(CacheUtils.existsMap("test"));
        CacheUtils.deleteMap("test");

        // 测试使用通配符模糊操作
        for (int i = 0; i < 100; i++) {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("test" + i, "test" + i);
            CacheUtils.put("test" + i, stringStringHashMap);
        }
        System.out.println(CacheUtils.getKeysByPattern("test*"));
        System.out.println(CacheUtils.getKeyValuesByPattern("test*"));
        CacheUtils.deleteByPattern("test*");
    }

    /**
     * 测试限流工具类
     */
    @Test
    void testRateLimitUtils() throws InterruptedException {
        for (int i = 0; i < 5; i++) {
            try {
                RateLimitUtils.doRateLimit("test1", new TimeModel(1L, TimeUnit.SECONDS), 2L, 1L);
                System.out.println(i);
            } catch (CustomizeRedissonException e) {
                System.out.println("请求太多，请稍后");
            }
        }
        ThreadUtils.sleep(Duration.ofSeconds(2));
        for (int i = 0; i < 10; i++) {
            try {
                RateLimitUtils.doRateLimitAndExpire("test2", new TimeModel(1L, TimeUnit.SECONDS), 2L, 1L, new TimeModel(1L, TimeUnit.SECONDS));
                System.out.println(i);
            } catch (CustomizeRedissonException e) {
                System.out.println("请求太多，请稍后");
            }
        }
    }

    /**
     * 测试无论获取锁成功与否均无返回值的分布式锁
     */
    @Test
    void testLockUtils1() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < 10; i++) {
                int finalI = i + 1;
                LockUtils.lockEvent("test", (VoidFunction) () -> {
                    System.out.println("子线程第" + finalI + "次拿到锁");
                    try {
                        ThreadUtils.sleep(Duration.ofMillis(1000));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("子线程第" + finalI + "次释放锁");
                });
            }
            return "ok";
        });
        for (int i = 0; i < 10; i++) {
            int finalI = i + 1;
            LockUtils.lockEvent("test", (VoidFunction) () -> {
                System.out.println("主线程第" + finalI + "次拿到锁");
                try {
                    ThreadUtils.sleep(Duration.ofMillis(1000));
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("主线程第" + finalI + "次释放锁");
            });
        }
        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试无论获取锁成功与否均带有boolean类型返回值的分布式锁
     * 这个和测试无论获取锁成功与否均带有自定义类型返回值的分布式锁大同小异
     */
    @Test
    void testLockUtils2() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            for (int i = 0; i < 10; i++) {
                int finalI = i + 1;
                boolean result = LockUtils.lockEvent("test", (SuccessFunction) () -> {
                    System.out.println("子线程第" + finalI + "次拿到锁");
                    System.out.println("子线程第" + finalI + "次释放锁");
                });
                if (!result) {
                    System.out.println("子线程第" + finalI + "次没拿到锁");
                }
            }
            return "ok";
        });
        for (int i = 0; i < 10; i++) {
            int finalI = i + 1;
            boolean result = LockUtils.lockEvent("test", (SuccessFunction) () -> {
                System.out.println("主线程第" + finalI + "次拿到锁");
                System.out.println("主线程第" + finalI + "次释放锁");
            });
            if (!result) {
                System.out.println("主线程第" + finalI + "次没拿到锁");
            }
        }
        try {
            System.out.println(future.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 测试队列工具类
     * 简单的入队和出队
     */
    @Test
    void testQueueUtils1() {
        if (QueueUtils.offer("test1", 1) &&
                QueueUtils.offer("test1", 2) &&
                QueueUtils.offer("test1", 3)) {
            System.out.println("QueueData-poll: " + QueueUtils.poll("test1"));
            System.out.println("QueueData-peek: " + QueueUtils.peek("test1"));
            System.out.println("QueueData-poll: " + QueueUtils.poll("test1"));
            System.out.println("QueueData-take: " + QueueUtils.take("test1"));
        }
    }

    /**
     * 测试队列工具类
     * 监听功能
     */
    @Test
    void testQueueUtils2() throws InterruptedException {
        CompletableFuture.runAsync(() -> {
            QueueUtils.listen("test2", queueData -> {
                System.out.println("QueueData: " + queueData);
            });
        });
        for (int i = 0; i < 10; i++) {
            QueueUtils.offer("test2", i);
            Thread.sleep(1000);
        }
    }

}