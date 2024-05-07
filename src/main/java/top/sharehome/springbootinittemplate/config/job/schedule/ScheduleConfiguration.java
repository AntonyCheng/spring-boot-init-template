package top.sharehome.springbootinittemplate.config.job.schedule;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import top.sharehome.springbootinittemplate.config.job.schedule.condition.ScheduleCycleCondition;
import top.sharehome.springbootinittemplate.config.job.schedule.properties.ScheduleProperties;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.Executors;

/**
 * SpringBoot定时任务配置
 * Schedule注解管理的任务虽然时异步执行的，但是不同的任务之间不是并行的，在其中一个定时任务没有执行完之前，其他的定时任务即使是到了执行时间，也是不会执行的；
 * 这个配置主要解决定时任务之间并发执行的问题，即单独为定时任务引入线程池
 *
 * @author AntonyCheng
 */
@Slf4j
@AllArgsConstructor
@EnableConfigurationProperties(ScheduleProperties.class)
@Configuration
@Conditional(ScheduleCycleCondition.class)
public class ScheduleConfiguration implements SchedulingConfigurer {

    private final ScheduleProperties scheduleProperties;

    /**
     * 重写任务配置
     *
     * @param taskRegistrar 任务注册器
     */
    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 设置线程池
        taskRegistrar.setScheduler(Executors.newScheduledThreadPool(scheduleProperties.getCycle().getThreadPool()));
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initDi() {
        log.info("############ {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}