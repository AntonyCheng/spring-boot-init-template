package top.sharehome.springbootinittemplate.config.job;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * xxl-job配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "xxl.job")
public class XxlJobProperties {

    /**
     * 是否开启
     */
    private boolean enable;

    /**
     * xxl-job监控面板地址
     */
    private String adminAddresses;

    /**
     * xxl-job token
     */
    private String accessToken;

    /**
     * 执行器相关属性
     */
    private Executor executor;

    @Data
    @NoArgsConstructor
    public static class Executor {

        /**
         * 执行器AppName：执行器心跳注册分组依据；为空则关闭自动注册
         */
        private String appname;

        /**
         * # 执行器端口号
         */
        private int port;

        /**
         * 执行器注册
         */
        private String address;

        /**
         * 执行器IP
         */
        private String ip;

        /**
         * 执行器运行日志文件存储磁盘路径
         */
        private String logpath;

        /**
         * 执行器日志文件保存天数
         */
        private int logretentiondays;

    }
}
