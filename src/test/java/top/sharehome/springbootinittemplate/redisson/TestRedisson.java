package top.sharehome.springbootinittemplate.redisson;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ThreadUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeReturnException;
import top.sharehome.springbootinittemplate.utils.redisson.cache.CacheUtils;
import top.sharehome.springbootinittemplate.utils.redisson.lock.LockUtils;
import top.sharehome.springbootinittemplate.utils.redisson.rateLimit.RateLimitUtils;
import top.sharehome.springbootinittemplate.utils.redisson.lock.function.SuccessFunction;
import top.sharehome.springbootinittemplate.utils.redisson.lock.function.VoidFunction;

import java.time.Duration;
import java.util.*;

/**
 * 测试缓存类
 *
 * @author AntonyCheng
 */
@SpringBootTest
@Slf4j
public class TestRedisson {

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
        System.out.println(CacheUtils.getNumberDoubleValue("test"));
        System.out.println(CacheUtils.existsNumber("test"));
        CacheUtils.deleteNumber("test");

        // 测试List
        List<String> l = new ArrayList<String>() {
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
        Set<String> s = new HashSet<String>() {
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
        Map<String, String> m = new HashMap<String, String>() {
            {
                put("test1", "test1");
                put("test2", "test2");
            }
        };
        CacheUtils.putMap("test", m);
        System.out.println(CacheUtils.getMap("test"));
        System.out.println(CacheUtils.existsMap("test"));
        CacheUtils.deleteMap("test");
    }

    /**
     * 测试限流工具类
     */
    @Test
    void testRateLimitUtils() throws InterruptedException {
        try {
            for (int i = 0; i < 5; i++) {
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }
        } catch (CustomizeReturnException e) {
            System.out.println("请求太多，请稍后");
        }
        ThreadUtils.sleep(Duration.ofSeconds(2));
        try {
            for (int i = 0; i < 10; i++) {
                RateLimitUtils.doRateLimit("test");
                System.out.println(i);
            }
        } catch (CustomizeReturnException e) {
            System.out.println("请求太多，请稍后");
        }
    }

    /**
     * 测试无论获取锁成功与否均无返回值的分布式锁
     */
    @Test
    void testLockUtils1() {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();
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
        while (true) {
        }
    }

    /**
     * 测试无论获取锁成功与否均带有boolean类型返回值的分布式锁
     * 这个和测试无论获取锁成功与否均带有自定义类型返回值的分布式锁大同小异
     */
    @Test
    void testLockUtils2() {
        new Thread(new Runnable() {
            @Override
            public void run() {
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
            }
        }).start();
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
        while (true) {
        }
    }

}