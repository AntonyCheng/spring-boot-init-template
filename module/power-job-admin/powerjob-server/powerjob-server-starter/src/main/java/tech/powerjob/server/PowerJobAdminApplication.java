package tech.powerjob.server;

import tech.powerjob.server.common.utils.PropertyUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * powerjob-server entry
 *
 * @author tjq
 * @since 2020/3/29
 */
@Slf4j
@EnableScheduling
@SpringBootApplication
public class PowerJobAdminApplication {

    private static final String TIPS = "\n\n" +
            "******************* PowerJob Tips *******************\n" +
            "如果应用无法启动，建议仔细阅读以下文档来解决:\n" +
            "if server can't startup, we recommend that you read the documentation to find a solution:\n" +
            "https://www.yuque.com/powerjob/guidence/problem\n" +
            "******************* PowerJob Tips *******************\n\n";

    public static void main(String[] args) {

        pre();

        // Start SpringBoot application.
        try {
            SpringApplication.run(PowerJobAdminApplication.class, args);
        } catch (Throwable t) {
            log.error(TIPS);
            throw t;
        }
    }

    private static void pre() {
        log.info(TIPS);
        PropertyUtils.init();
    }

}
