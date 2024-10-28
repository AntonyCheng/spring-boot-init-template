package top.sharehome.springbootinittemplate.utils.redisson.queue.function;

/**
 * 分布式队列中所用到的函数式接口
 *
 * @author AntonyCheng
 */
@FunctionalInterface
public interface QueueFunction {

    void execute(Object obj);

}
