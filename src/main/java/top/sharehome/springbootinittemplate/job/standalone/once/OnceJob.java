package top.sharehome.springbootinittemplate.job.standalone.once;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 全量任务
 * 实现CommandLineRunner接口，可实现系统初始化操作
 * todo 打开Component注解即可容器注入然后执行
 *
 * @author AntonyCheng
 */
@Component
@Slf4j
@ConditionalOnProperty(prefix = "schedule.once", name = "enable", havingValue = "true")
public class OnceJob implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("Running Application Init:{}", new Date().toString());
    }

}
