package top.sharehome.springbootinittemplate.utils.redisson.queue;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RedissonClient;
import top.sharehome.springbootinittemplate.common.base.ReturnCode;
import top.sharehome.springbootinittemplate.config.bean.SpringContextHolder;
import top.sharehome.springbootinittemplate.exception.customize.CustomizeRedissonException;
import top.sharehome.springbootinittemplate.utils.redisson.KeyPrefixConstants;
import top.sharehome.springbootinittemplate.utils.redisson.queue.function.QueueFunction;

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
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 元素进队
     *
     * @param queueName 队列名称
     * @param data      进队元素
     * @param <T>       泛型
     */
    public static <T> Boolean offer(String queueName, T data) {
        RBlockingQueue<T> queue = REDISSON_CLIENT.getBlockingQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
        return queue.offer(data);
    }

    /**
     * 元素出队
     *
     * @param queueName 队列名称
     * @param <T>       泛型
     */
    public static <T> T poll(String queueName) {
        RBlockingQueue<T> queue = REDISSON_CLIENT.getBlockingQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
        return queue.poll();
    }

    /**
     * 元素出队，空队即等待
     *
     * @param queueName 队列名称
     * @param <T>       泛型
     */
    public static <T> T take(String queueName) {
        try {
            RBlockingQueue<T> queue = REDISSON_CLIENT.getBlockingQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
            return queue.take();
        } catch (InterruptedException e) {
            throw new CustomizeRedissonException(ReturnCode.REDISSON_MQ_SERVICE_ERROR);
        }
    }

    /**
     * 偷看元素
     *
     * @param queueName 队列名称
     * @param <T>       泛型
     */
    public static <T> T peek(String queueName) {
        RBlockingQueue<T> queue = REDISSON_CLIENT.getBlockingQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
        return queue.peek();
    }

    /**
     * 监听队列
     *
     * @param queueName 队列名称
     * @param function  处理队列元素
     * @param <T>       泛型
     */
    public static <T> void listen(String queueName, QueueFunction function) {
        THREAD_POOL.execute(() -> {
            RBlockingQueue<T> queue = REDISSON_CLIENT.getBlockingQueue(KeyPrefixConstants.QUEUE_PREFIX + queueName);
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    T data = queue.poll(1, TimeUnit.SECONDS);
                    if (Objects.nonNull(data)) {
                        function.execute(data);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    throw new CustomizeRedissonException(ReturnCode.REDISSON_MQ_SERVICE_ERROR);
                }
            }
        });
    }

}
