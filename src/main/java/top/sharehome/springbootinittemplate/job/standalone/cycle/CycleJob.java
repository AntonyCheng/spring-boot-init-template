package top.sharehome.springbootinittemplate.job.standalone.cycle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.job.schedule.condition.ScheduleCycleCondition;

import java.util.Date;

/**
 * 循环任务
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
@Conditional(ScheduleCycleCondition.class)
public class CycleJob {

    /**
     * 固定间隔时间任务
     * 注解表示一分钟执行一次
     */
    @Scheduled(fixedDelay = 60 * 1000)
    public void fixedTimeJob() {
        log.info("Fixed time execution:{}", new Date());
    }

    /**
     * 定时任务
     * 注解使用Cron表达式(cron = "秒 分 时 天 月 周")
     */
    @Scheduled(cron = "0 * * * * *")
    public void scheduledTimeJob() {
        log.info("Scheduled time execution:{}", new Date());
    }

}