package top.sharehome.powerjobadmin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import tech.powerjob.server.common.utils.PropertyUtils;

/**
 * powerjob 启动程序
 *
 * @author yhan219
 */
@Slf4j
@EnableScheduling
@SpringBootApplication(scanBasePackages = "tech.powerjob.server")
public class PowerJobAdminApplication {

    public static void main(String[] args) {
        PropertyUtils.init();
        SpringApplication.run(tech.powerjob.server.PowerJobServerApplication.class, args);
    }

}
