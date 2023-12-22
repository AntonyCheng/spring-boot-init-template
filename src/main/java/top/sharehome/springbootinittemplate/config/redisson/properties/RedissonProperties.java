package top.sharehome.springbootinittemplate.config.redisson.properties;

import lombok.AllArgsConstructor;
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
     * 限流单位时间，单位：秒
     */
    private long limitRateInterval;

    /**
     * 限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     */
    private long limitRate;

    /**
     * 每个操作所要消耗的令牌数
     */
    private long limitPermits;

    /**
     * 单机服务配置
     */
    private SingleServerConfig singleServerConfig;

    /**
     * 集群服务配置
     */
    private ClusterServersConfig clusterServersConfig;

    /**
     * 单例服务配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleServerConfig {
        /**
         * 是否启动单机Redis
         */
        private boolean enableSingle;
        private String address;
        private int database;
        private String password;
        private int timeout;
        private int subscriptionConnectionPoolSize;
        private int connectionMinimumIdleSize;
        private int connectionPoolSize;
        private int idleConnectionTimeout;
    }

    /**
     * 集群服务配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClusterServersConfig {
        /**
         * 是否启动集群Redis
         */
        private boolean enableCluster;
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