package top.sharehome.springbootinittemplate.utils.redisson.queue;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RQueue;
import org.redisson.api.RedissonClient;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.queue.function.QueueFunction;

import java.io.Closeable;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 队列工具类
 *
 * @author AntonyCheng
 */
@Slf4j
public class QueueUtils {

    /**
     * 被封装的redisson客户端对象
     */
    private static final RedissonClient REDISSON_CLIENT = SpringContextHolder.getBean(RedissonClient.class);

    /**
     * 线程池
     */
    private static final ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(
            5,
            15,
            2L,
            TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(10),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    public static <T> void offer(String queueName, T t) {
        RQueue<T> queue = REDISSON_CLIENT.getQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
        queue.offer(t);
    }

    public static <T> T poll(String queueName) {
        RQueue<T> queue = REDISSON_CLIENT.getQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
        return queue.poll();
    }

    public static <T> void listen(String queueName, QueueFunction function) {
        THREAD_POOL.execute(() -> {
            RBlockingQueue<T> queue = REDISSON_CLIENT.getBlockingQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    T obj = queue.poll(1, TimeUnit.SECONDS);
                    if (Objects.nonNull(obj)) {
                        function.execute(obj);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    // 记录异常日志，避免线程终止
                    e.printStackTrace();
                }
            }
        });
    }

}
