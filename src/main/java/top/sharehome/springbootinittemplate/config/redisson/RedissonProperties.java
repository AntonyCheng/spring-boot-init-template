package top.sharehome.springbootinittemplate.config.redisson;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

/**
 * Redisson配置属性
 *
 * @author AntonyCheng
 */
@Data
@ConfigurationProperties(prefix = "redisson")
public class RedissonProperties {
    /**
     * 线程池数量,默认值 = 当前处理核数量 * 2
     */
    private int threads;

    /**
     * Netty线程池数量,默认值 = 当前处理核数量 * 2
     */
    private int nettyThreads;

    /**
     * 单机服务配置
     */
    private SingleServerConfig singleServerConfig;

    /**
     * 集群服务配置
     */
    private ClusterServersConfig clusterServersConfig;

    @Data
    @NoArgsConstructor
    public static class SingleServerConfig {
        private String address;
        private int database;
        private String password;
        private int timeout;
        private int subscriptionConnectionPoolSize;
        private int connectionMinimumIdleSize;
        private int connectionPoolSize;
        private int idleConnectionTimeout;
    }

    @Data
    @NoArgsConstructor
    public static class ClusterServersConfig {
        private List<String> nodeAddresses;
        private String password;
        private int masterConnectionMinimumIdleSize;
        private int masterConnectionPoolSize;
        private int slaveConnectionMinimumIdleSize;
        private int slaveConnectionPoolSize;
        private int idleConnectionTimeout;
        private int timeout;
        private int subscriptionConnectionPoolSize;
    }
}
