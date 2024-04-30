package top.sharehome.springbootinittemplate.job.standalone.once;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;
import top.sharehome.springbootinittemplate.config.job.schedule.condition.ScheduleOnceCondition;

import java.util.Date;

/**
 * 全量任务
 * 实现CommandLineRunner接口，可实现系统初始化操作
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
@Conditional(ScheduleOnceCondition.class)
public class OnceJob implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("Running Application Init:{}", new Date());
    }

}