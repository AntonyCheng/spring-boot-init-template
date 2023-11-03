package top.sharehome.springbootinittemplate.job.standalone.once;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import java.util.Date;

/**
 * 一次任务
 * 实现CommandLineRunner接口，可实现系统初始化操作
 * todo：打开Component注解即可DI执行
 *
 * @author AntonyCheng
 */
//@Component
@Slf4j
public class OnceJob implements CommandLineRunner {

    @Override
    public void run(String... args) throws Exception {
        log.info("Running Application Init:{}", new Date().toString());
    }

}
