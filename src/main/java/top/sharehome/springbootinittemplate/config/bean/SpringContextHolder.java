package top.sharehome.springbootinittemplate.config.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Spring容器上下文类
 *
 * @author AntonyCheng
 */
@Component
public class SpringContextHolder implements ApplicationContextAware {

    /**
     * 以静态变量保存ApplicationContext,可在任意代码中取出ApplicaitonContext.
     */
    private static ApplicationContext context;

    /**
     * 实现ApplicationContextAware接口的context注入函数, 将其存入静态变量.
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextHolder.context = applicationContext;
    }

    /**
     * 获取applicationContext
     *
     * @return 返回结果
     */
    public ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name Bean名称
     * @return 返回结果
     */
    public static Object getBean(String name) {
        return context.getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz Bean类
     * @param <T>   泛型T
     * @return 返回结果
     */
    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz Bean类
     * @param <T>   泛型T
     * @return 返回结果
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name  Bean名称
     * @param clazz Bean类
     * @param <T>   泛型T
     * @return 返回结果
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

}