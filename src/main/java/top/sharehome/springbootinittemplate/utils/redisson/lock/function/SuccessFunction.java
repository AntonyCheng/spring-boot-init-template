package top.sharehome.springbootinittemplate.utils.redisson.lock.function;

/**
 * 分布式锁中所用到的函数式接口
 *
 * @author AntonyCheng
 */
@FunctionalInterface
public interface SuccessFunction {

    void method();

}