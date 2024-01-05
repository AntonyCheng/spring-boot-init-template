package top.sharehome.springbootinittemplate.utils.redisson.lock.function;

/**
 * 分布式锁中所用到的函数式接口
 * todo：修改成Runnable类型的函数式接口即可，不用自己写
 *
 * @author AntonyCheng
 */
@FunctionalInterface
public interface SuccessFunction {

    void method();

}