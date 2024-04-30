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
    private Integer threads = 4;

    /**
     * Netty线程池数量,默认值 = 当前处理核数量 * 2
     */
    private Integer nettyThreads = 8;

    /**
     * 限流单位时间，单位：秒
     */
    private Long limitRateInterval = 1L;

    /**
     * 限流单位时间内访问次数，也能看做单位时间内系统分发的令牌数
     */
    private Long limitRate = 2L;

    /**
     * 每个操作所要消耗的令牌数
     */
    private Long limitPermits = 1L;

    /**
     * 单机服务配置
     */
    private SingleServerConfig singleServerConfig;

    /**
     * 集群服务配置
     */
    private ClusterServersConfig clusterServersConfig;

    /**
     * 单机服务配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SingleServerConfig {

        /**
         * 是否启动单机Redis（Redisson）缓存
         */
        private Boolean enableSingle = false;

        /**
         * 单机地址（一定要在redis协议下）
         */
        private String address;

        /**
         * # 数据库索引
         */
        private Integer database;

        /**
         * 密码（考虑是否需要密码）
         */
        private String password;

        /**
         * 命令等待超时，单位：毫秒
         */
        private Integer timeout = 3000;

        /**
         * 发布和订阅连接池大小
         */
        private Integer subscriptionConnectionPoolSize = 25;

        /**
         * 最小空闲连接数
         */
        private Integer connectionMinimumIdleSize = 8;

        /**
         * 连接池大小
         */
        private Integer connectionPoolSize = 32;

        /**
         * 连接空闲超时，单位：毫秒
         */
        private Integer idleConnectionTimeout = 10000;

    }

    /**
     * 集群服务配置
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClusterServersConfig {

        /**
         * 是否启动集群redisson（Redisson）缓存
         */
        private Boolean enableCluster = false;

        /**
         * redis集群节点（一定要在redis协议下）
         */
        private List<String> nodeAddresses;

        /**
         * 密码（考虑是否需要密码）
         */
        private String password;

        /**
         * master最小空闲连接数
         */
        private Integer masterConnectionMinimumIdleSize = 16;

        /**
         * master连接池大小
         */
        private Integer masterConnectionPoolSize = 32;

        /**
         * slave最小空闲连接数
         */
        private Integer slaveConnectionMinimumIdleSize = 16;

        /**
         * slave连接池大小
         */
        private Integer slaveConnectionPoolSize = 32;

        /**
         * 连接空闲超时，单位：毫秒
         */
        private Integer idleConnectionTimeout = 10000;

        /**
         * 命令等待超时，单位：毫秒
         */
        private Integer timeout = 3000;

        /**
         * 发布和订阅连接池大小
         */
        private Integer subscriptionConnectionPoolSize = 25;

    }

}