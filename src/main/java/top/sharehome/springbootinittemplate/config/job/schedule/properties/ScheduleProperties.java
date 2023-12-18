package top.sharehome.springbootinittemplate.config.job.schedule.properties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SpringBoot任务调度配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "schedule")
public class ScheduleProperties {

    /**
     * 循环任务
     */
    private Cycle cycle;

    /**
     * 全量任务
     */
    private Once once;

    /**
     * 循环任务配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Cycle {
        /**
         * 是否开启循环任务
         */
        private Boolean enable = false;

        /**
         * 线程池大小（开启则必填）
         */
        private Integer threadPool = 10;
    }

    /**
     * 全量任务配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Once {
        /**
         * 是否开启全量任务
         */
        private Boolean enable = false;
    }

}